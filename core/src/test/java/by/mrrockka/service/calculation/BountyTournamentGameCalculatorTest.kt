package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.game
import by.mrrockka.builder.player
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
        val players = bountyPlayers(size, buyin, bounty)
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = fromBounties + toBounties
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, toBounties.person))
        }.bountyTournament()

        val actual = calculator.calculate(game)
        val expect = listOf(
                Payout(
                        creditor = toBounties,
                        debtors = fromBounties
                                .filterNot { it == toBounties }
                                .map { Debtor(it, buyin + bounty) }
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
        val players = bountyPlayers(size = 10, buyin = buyin, bounty = bounty) + bountyPlayer(buyin = buyin, bounty = bounty, entries = 3) + bountyPlayer(buyin = buyin, bounty = bounty, entries = 4)
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = fromBounties + toBounties
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, toBounties.person))
        }.bountyTournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given equal entries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(size = 10, buyin, bounty)

        val (firstPlace, firstPlaceBounties) = players.drop(2).dropLast(2).bountyToWinner(players[0], bounty)
        val (secondPlace, secondPlaceBounties) = players.drop(8).bountyToWinner(players[1], bounty)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = firstPlaceBounties + secondPlaceBounties + firstPlace + secondPlace
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, firstPlace.person),
                    FinalPlace(2, secondPlace.person),
            )
        }.bountyTournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given equal entries and one prize positions and winner has only one bounty should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(size = 10, buyin, bounty)

        val (noPlace, noPlaceBounties) = players.drop(2).bountyToWinner(players[1], bounty)
        val (firstPlace, firstPlaceBounties) = noPlace.bountyToWinner(players[0], bounty)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = noPlaceBounties + firstPlaceBounties + firstPlace
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, firstPlace.person))
        }.bountyTournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
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
        val players = listOf(bountyPlayer(buyin = buyin, bounty = bounty, entries = 5)) + bountyPlayers(size = 2, buyin = buyin, bounty = bounty)
        val (first, firstBounties) = players.bountyToWinner(winner = players[0], bounty = bounty, entries = 1)
        val (third, thirdBounties) = first.bountyToWinner(winner = firstBounties.last(), bounty = bounty, entries = 4)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = (thirdBounties + firstBounties + first + third).reduceBounties()
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("50")),
                    PositionPrize(2, BigDecimal("50")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }.bountyTournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given prize amounts has decimal points should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val bounty = BigDecimal("10")
        val players = bountyPlayers(size = 11, buyin = buyin, bounty = bounty)
        val (toBounties, fromBounties) = players.bountyToWinner(players[0], bounty)

        val game = game {
            this.buyIn = buyin
            this.bounty = bounty
            this.players = fromBounties + toBounties
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("75")),
                    PositionPrize(2, BigDecimal("25")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }.bountyTournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
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

    fun bountyPlayers(size: Int, buyin: BigDecimal = BigDecimal("10"), bounty: BigDecimal = BigDecimal("10")): List<BountyPlayer> =
            player {
                this.buyin = buyin
                this.bounty = bounty
            }.bountyBatch(size)

    fun bountyPlayer(buyin: BigDecimal = BigDecimal("10"), bounty: BigDecimal = BigDecimal("10"), entries: Int = 1): BountyPlayer = player {
        this.buyin = buyin
        this.bounty = bounty
        this.entriesSize = entries
    }.bounty()


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
                                                    Bounty(from = player.person.id, to = winner.person.id, amount = bounty)
                                                } else null
                                            }
                                            .filterNotNull(),
                    )
                }
        val toBounty = winner.copy(bounties = fromBounties.flatMap { it.bounties }.filter { bounty -> bounty.to == winner.person.id })
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
