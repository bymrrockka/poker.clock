package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.game
import by.mrrockka.builder.player
import by.mrrockka.domain.*
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
    fun `given some reentries and one prize place should calculate`(approver: Approver) {
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
    fun `given equal entries and two prize positions should calculate`(approver: Approver) {
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
    fun `given some reentries and two prize positions should calculate`(approver: Approver) {
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
    fun `given winners has reentries should calculate`(approver: Approver) {
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
    fun `given winners has reentries and prize doesn't cover debt should calculate payouts`(approver: Approver) {
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
    fun `given prize amounts has decimal points should calculate`(approver: Approver) {
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

    fun tournamentPlayers(size: Int, buyin: BigDecimal = BigDecimal("10")): List<Player> =
            (0..<size)
                    .asSequence()
                    .map { player { this.buyin = buyin }.tournament() }
                    .toList()

    fun tournamentPlayer(buyin: BigDecimal = BigDecimal("10"), entries: Int = 1): Player = player {
        this.buyin = buyin
        this.entriesSize = entries
    }.tournament()
}
