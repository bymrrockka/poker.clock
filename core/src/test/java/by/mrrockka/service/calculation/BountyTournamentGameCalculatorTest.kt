package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.bountyGame
import by.mrrockka.builder.bountyPlayer
import by.mrrockka.builder.bountyPlayers
import by.mrrockka.builder.plus
import by.mrrockka.domain.Bounty
import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Payout
import by.mrrockka.domain.PositionPrize
import by.mrrockka.service.GameCalculator
import com.oneeyedmen.okeydoke.Approver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

class BountyTournamentGameCalculatorTest : AbstractTest() {
    private val calculator: GameCalculator = GameCalculator()

    @ParameterizedTest
    @MethodSource("playerSize")
    fun `given equal entries and one prize place should calculate`(size: Int) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(size) {
            buyin(buyin)
            bounty(bounty)
        }
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players(fromBounties + toBounties)
            prizePool(PositionPrize(1, BigDecimal("100")))
            finalePlaces(FinalPlace(1, toBounties.person))
        }

        val actual = calculator.calculate(game)
        val expect = listOf(
                Payout(
                        creditor = toBounties.person,
                        debtors = fromBounties
                                .filterNot { it == toBounties }
                                .map { Debtor(it.person, buyin + bounty) }
                                .reversed(),
                        total = BigDecimal("20") * (players.size - 1).toBigDecimal(),
                ),
        )

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given some reentries and one prize place should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(10) {
            buyin(buyin)
            bounty(bounty)
        } + bountyPlayer {
            buyin(buyin)
            bounty(bounty)
            entries(3)
        } + bountyPlayer {
            buyin(buyin)
            bounty(bounty)
            entries(4)
        }
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players(fromBounties + toBounties)
            prizePool(PositionPrize(1, BigDecimal("100")))
            finalePlaces(FinalPlace(1, toBounties.person))
        }

        approver.assertApproved(calculator.calculate(game).simplify(players).toJsonString())
    }

    @Test
    fun `given equal entries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(10) {
            buyin(buyin)
            bounty(bounty)
        }

        val (firstPlace, firstPlaceBounties) = players.drop(2).dropLast(2).bountyToWinner(players[0], bounty)
        val (secondPlace, secondPlaceBounties) = players.drop(8).bountyToWinner(players[1], bounty)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players(firstPlaceBounties + secondPlaceBounties + firstPlace + secondPlace)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, firstPlace.person),
                    FinalPlace(2, secondPlace.person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify(players).toJsonString())
    }

    @Test
    fun `given equal entries and one prize positions and winner has only one bounty should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(10) {
            buyin(buyin)
            bounty(bounty)
        }

        val (noPlace, noPlaceBounties) = players.drop(2).bountyToWinner(players[1], bounty)
        val (firstPlace, firstPlaceBounties) = noPlace.bountyToWinner(players[0], bounty)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players(noPlaceBounties + firstPlaceBounties + firstPlace)
            prizePool(PositionPrize(1, BigDecimal("100")))
            finalePlaces(FinalPlace(1, firstPlace.person))
        }

        approver.assertApproved(calculator.calculate(game).simplify(players).toJsonString())
    }


    @Test
    fun `given winners has reentries and still in debt should calculate`(approver: Approver) {
        /* test setup
         * Given buyin and bounty by 10
         * And first and second places receive 50% of pot
         * And first player enter a game 5 times
         * And two more players enter game
         * And first player in first place with 35 pot
         * And second player in second place with 35 pot
         * And third player knocked out first player 4 times
         * And first player knocked out second and third 1 time
         * And first player saved his bounty
         * When game calculated
         * Then total money in prize is 7 * 10 = 70
         * And total bounty is 7 * 10 = 70
         * And first player total is (5 * 2 * -10) + 35 + (3 * 10) = -35
         * And second player total is (2 * -10) + 35 = 15
         * And third player total is (2 * -10) + (4 * 10) = 20
         * */

        val bounty = BigDecimal("10")
        val buyin = BigDecimal("10")
        val players = bountyPlayer {
            buyin(buyin)
            bounty(bounty)
            entries(5)
        } + bountyPlayers(2) {
            buyin(buyin)
            bounty(bounty)
        }

        val (first, firstBounties) = players.bountyToWinner(winner = players[0], bounty = bounty, entries = 1)
        val (third, thirdBounties) = first.bountyToWinner(winner = firstBounties.last(), bounty = bounty, entries = 4)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players((thirdBounties + firstBounties + first + third).reduceBounties())
            prizePool(
                    PositionPrize(1, BigDecimal("50")),
                    PositionPrize(2, BigDecimal("50")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify(players).toJsonString())
    }

    @Test
    fun `given prize amounts has decimal points should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(11) {
            buyin(buyin)
            bounty(bounty)
        }
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = bountyGame {
            buyIn(buyin)
            bounty(bounty)
            players(fromBounties + toBounties)
            prizePool(
                    PositionPrize(1, BigDecimal("75")),
                    PositionPrize(2, BigDecimal("25")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify(players).toJsonString())
    }

    companion object {
        @JvmStatic
        private fun playerSize(): Stream<Arguments?> {
            return Stream.of(
                    Arguments.of(2),
                    Arguments.of(4),
                    Arguments.of(10),
                    Arguments.of(20),
                    Arguments.of(100),
            )
        }
    }

    fun BountyPlayer.bountyToWinner(winner: BountyPlayer, bounty: BigDecimal, entries: Int = -1): Pair<BountyPlayer, List<BountyPlayer>> = listOf(this).bountyToWinner(winner, bounty, entries)

    fun List<BountyPlayer>.bountyToWinner(winner: BountyPlayer, bounty: BigDecimal, entries: Int = -1): Pair<BountyPlayer, List<BountyPlayer>> {
        val fromBounties = this
                .filter { it != winner }
                .map { player ->
                    player.copy(
                            bounties = player.bounties +
                                    player.entries
                                            .mapIndexed { index, _ ->
                                                if (entries == -1 || index < entries) {
                                                    Bounty(from = player.person, to = winner.person, amount = bounty)
                                                } else null
                                            }
                                            .filterNotNull(),
                    )
                }
        val toBounty = winner.copy(bounties = fromBounties.flatMap { it.bounties }.filter { bounty -> bounty.to == winner.person })
        return toBounty to fromBounties
    }

    fun List<BountyPlayer>.reduceBounties(): List<BountyPlayer> =
            this.groupBy { it.person }.entries
                    .map { (_, bounties) ->
                        val biggest = bounties.maxBy { bounty -> bounty.bounties.size }
                        val other = (bounties - biggest).flatMap { it.bounties }
                        when {
                            biggest.bounties.none { other.contains(it) } -> biggest.copy(bounties = biggest.bounties + other)
                            biggest.bounties.containsAll(other) -> biggest
                            else -> biggest.copy(bounties = biggest.bounties + other.filterNot { biggest.bounties.contains(it) })
                        }
                    }
}
