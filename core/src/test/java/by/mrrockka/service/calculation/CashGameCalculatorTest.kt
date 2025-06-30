package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.game
import by.mrrockka.builder.player
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Payout
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
        val players = cashPlayers(size, buyin)
        val withdrawalPlayer = players[0].addWithdrawals(withdrawal, size)

        val game = game {
            this.buyIn = buyin
            this.players = players.drop(1) + withdrawalPlayer
        }.cash()

        val actual = calculator.calculate(game)
        val expect = listOf(Payout(
                creditor = withdrawalPlayer,
                debtors = players.drop(1)
                        .map { Debtor(it, buyin) }
                        .reversed(),
                total = BigDecimal("10") * (players.size - 1).toBigDecimal()
        ))

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given equal entries and two players win pot should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayers(size = 10, buyin)
        val first = players[0].addWithdrawals(withdrawal, 5)
        val second = players[1].addWithdrawals(withdrawal, 5)

        val game = game {
            this.buyIn = buyin
            this.players = players.drop(2) + first + second
        }.cash()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given equal entries and withdrawals should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = cashPlayers(size = 10, buyin)
                .map { it.addWithdrawals(withdrawal, 1) }

        val game = game {
            this.buyIn = buyin
            this.players = players
        }.cash()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given player enters many times and wins a pot should calculate`(approver: Approver) {
        val buyin = BigDecimal("10")
        val withdrawal = BigDecimal("10")
        val players = listOf(cashPlayer(buyin, 5), cashPlayer(buyin, 1))
        val winner = players[0].addWithdrawals(withdrawal, 6)

        val game = game {
            this.buyIn = buyin
            this.players = players.drop(1) + winner
        }.cash()

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
                    Arguments.of(100)
            )
        }
    }

    fun cashPlayer(buyin: BigDecimal = BigDecimal("10"), entries: Int): CashPlayer =
            player {
                this.buyin = buyin
                this.bounty = bounty
                this.entriesSize = entries
            }.cash()

    fun cashPlayers(size: Int, buyin: BigDecimal = BigDecimal("10")): List<CashPlayer> =
            player {
                this.buyin = buyin
                this.bounty = bounty
            }.cashBatch(size)

    fun CashPlayer.addWithdrawals(withdrawal: BigDecimal, size: Int): CashPlayer = this.copy(withdrawals = (0..<size).map { withdrawal })

}
