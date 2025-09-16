package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.cashGame
import by.mrrockka.builder.cashPlayer
import by.mrrockka.builder.cashPlayers
import by.mrrockka.builder.plus
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Payout
import by.mrrockka.service.GameCalculator
import com.oneeyedmen.okeydoke.Approver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

class CashGameCalculatorTest : AbstractTest() {
    private val calculator: GameCalculator = GameCalculator()

    @ParameterizedTest
    @MethodSource("playerSize")
    fun `given equal entries and one player wins pot should calculate`(size: Int) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayers(size) { buyin(buyin) }
        val withdrawalPlayer = players[0].addWithdrawals(withdrawal, size)

        val game = cashGame {
            buyIn(buyin)
            players(players.drop(1) + withdrawalPlayer)
        }

        val actual = calculator.calculate(game)
        val expect = listOf(
                Payout(
                        creditor = withdrawalPlayer,
                        debtors = players.drop(1)
                                .map { Debtor(it, buyin) }
                                .reversed(),
                        total = BigDecimal("10") * (players.size - 1).toBigDecimal(),
                ),
        )

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given equal entries and two players win pot should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayers(10) { buyin(buyin) }
        val first = players[0].addWithdrawals(withdrawal, 5)
        val second = players[1].addWithdrawals(withdrawal, 5)

        val game = cashGame {
            buyIn(buyin)
            players(players.drop(2) + first + second)
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given equal entries and withdrawals should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayers(10) { buyin(buyin) }
                .map { it.addWithdrawals(withdrawal, 1) }

        val game = cashGame {
            buyIn(buyin)
            players(players)
        }

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given player enters many times and wins a pot should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayer {
            buyin(buyin)
            entries(5)
        } + cashPlayer {
            buyin(buyin)
            entries(1)
        }
        val winner = players[0].addWithdrawals(withdrawal, 6)

        val game = cashGame {
            buyIn(buyin)
            players(players.drop(1) + winner)
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

    fun CashPlayer.addWithdrawals(withdrawal: BigDecimal, size: Int): CashPlayer = this.copy(withdrawals = (0..<size).map { withdrawal })
}
