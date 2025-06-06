package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.game
import by.mrrockka.builder.player
import by.mrrockka.domain.*
import com.oneeyedmen.okeydoke.Approver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

class GameCalculatorTest : AbstractTest() {
    private val calculator: GameCalculator = GameCalculator()

    @ParameterizedTest
    @MethodSource("playerSize")
    fun `given players entry equally when one prize place should calculate payouts`(size: Int) {
        val buyin = BigDecimal("10")
        val players = player { this.buyin = buyin }.tournamentBatch(size)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, players[0]))
        }.tournament()

        val actual = calculator.calculate(game)
        val expect = listOf(Payout(
                creditor = players[0],
                debtors = players
                        .filterNot { it == players[0] }
                        .map { Debtor(it, buyin) }
                        .reversed(),
                total = BigDecimal("10") * (players.size - 1).toBigDecimal()
        ))

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given players entry with different amounts when one prize place should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size = 10, buyin) + tournamentPlayer(buyin, 3) + tournamentPlayer(buyin, 4)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, players[0]))
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given players with equal entries when there are more then one prize positions should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size = 10, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given players with not equal entries when there are more then one prize positions should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size = 10, buyin) + tournamentPlayer(buyin, 3) + tournamentPlayer(buyin, 4)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given winners has more than one entry should calculate prize pool including entries`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = listOf(tournamentPlayer(buyin, 3), tournamentPlayer(buyin, 4)) + tournamentPlayers(size = 10, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given winners has more than one entry and one still in debt should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = listOf(tournamentPlayer(buyin, 3), tournamentPlayer(buyin, 4)) + tournamentPlayers(size = 10, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("90")),
                    PositionPrize(2, BigDecimal("10")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given prize pool amounts result has decimal points should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size = 11, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("75")),
                    PositionPrize(2, BigDecimal("25")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    companion object {
        private val BUY_IN: BigDecimal = BigDecimal.valueOf(20)

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
}

fun tournamentPlayers(size: Int, buyin: BigDecimal = BigDecimal("10")): List<Player> =
        (0..<size)
                .asSequence()
                .map { player { this.buyin = buyin }.tournament() }
                .toList()

fun tournamentPlayer(buyin: BigDecimal = BigDecimal("10"), entries: Int = 1): Player = player {
    this.buyin = buyin
    this.size = entries
}.tournament()

data class SimplePayout(
        val creditor: String,
        val entries: BigDecimal,
        val total: BigDecimal,
        val debtors: List<SimpleDebtor>,
)

data class SimpleDebtor(
        val debtor: String,
        val debt: BigDecimal,
        val entries: BigDecimal
)

internal fun List<Payout>.simplify(): List<SimplePayout> = map { payout ->
    SimplePayout(
            creditor = payout.creditor.person.nickname ?: fail("No creditor nickname found"),
            entries = payout.creditor.entries.total(),
            total = payout.total,
            debtors = payout.debtors.map { debtor ->
                SimpleDebtor(
                        debtor = debtor.player.person.nickname ?: fail("No debtor nickname found"),
                        debt = debtor.debt,
                        entries = debtor.player.entries.total()
                )
            }
    )
}