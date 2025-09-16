package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.plus
import by.mrrockka.builder.tournamentGame
import by.mrrockka.builder.tournamentPlayer
import by.mrrockka.builder.tournamentPlayers
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

class TournamentGameCalculatorTest : AbstractTest() {
    private val calculator: GameCalculator = GameCalculator()

    @ParameterizedTest
    @MethodSource("playerSize")
    fun `given equal entries and one prize place should calculate`(size: Int) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size) {
            buyin(buyin)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(PositionPrize(1, BigDecimal("100")))
            finalePlaces(FinalPlace(1, players[0].person))
        }

        val actual = calculator.calculate(game)
        val expect = listOf(
                Payout(
                        creditor = players[0],
                        debtors = players
                                .filterNot { it == players[0] }
                                .map { Debtor(it, buyin) }
                                .reversed(),
                        total = BigDecimal("10") * (players.size - 1).toBigDecimal(),
                ),
        )

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given some reentries and one prize place should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        } + tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(PositionPrize(1, BigDecimal("100")))
            finalePlaces(FinalPlace(1, players[0].person))
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given equal entries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given some reentries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        } + tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given winners has reentries should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        } + tournamentPlayers(10) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given winners has reentries and prize doesn't cover debt should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        } + tournamentPlayers(10) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("90")),
                    PositionPrize(2, BigDecimal("10")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given prize amounts has decimal points should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(11) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("75")),
                    PositionPrize(2, BigDecimal("25")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

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
}
