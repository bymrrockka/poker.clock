package by.mrrockka.validation.calculation

import by.mrrockka.builder.game
import by.mrrockka.builder.player
import by.mrrockka.domain.Bounty
import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Game
import by.mrrockka.validation.PreCalculationValidator
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal

@Deprecated("Move tests to scenario level")
class PreCalculationValidatorTest {
    private val preCalculationValidator = PreCalculationValidator()

    @ParameterizedTest
    @MethodSource("validGames")
    fun `game with all preconditions met does not throw any exceptions`(game: Game) {
        assertDoesNotThrow { preCalculationValidator.validateGame(game) }
    }

    @ParameterizedTest
    @MethodSource("invalidGames")
    fun `game with preconditions not met throws exception`(game: Game) {
        assertThrows<IllegalStateException> { preCalculationValidator.validateGame(game) }
    }

    companion object {
        @JvmStatic
        private fun validGames(): List<Game> = listOf(
                game { this.players = player().cashBatch(6).withdrawalsToFirst() }.cash(),
                game { this.players = player().tournamentBatch(6) }.prizeForFirst().tournament(),
                game { this.players = player().bountyBatch(6).bountiesToWinner() }.prizeForFirst().bountyTournament(),
        )

        @JvmStatic
        private fun invalidGames(): List<Game> {
            return listOf(
                    game { this.players = player().cashBatch(6) }.cash(),
                    game { this.players = player().cashBatch(6).withdrawalsToFirst(2) }.cash(),

                    game { this.players = player().tournamentBatch(6) }.tournament(),

                    game { this.players = player().bountyBatch(6) }.bountyTournament(),
                    game { this.players = player().bountyBatch(6).bountiesToWinner(2) }.prizeForFirst().bountyTournament(),
                    game { this.players = player().bountyBatch(6).bountiesToWinner() }.bountyTournament(),
            )
        }

        private fun List<CashPlayer>.withdrawalsToFirst(size: Int = this.size): List<CashPlayer> {
            val players = this.drop(1)
            val winner = first().copy(withdrawals = (0..<size).map { BigDecimal.TEN })
            return players + winner
        }

        private fun List<BountyPlayer>.bountiesToWinner(size: Int = this.size): List<BountyPlayer> {
            val players = this.drop(1)
                    .map { player ->
                        player.copy(
                                bounties = player.entries
                                        .map { Bounty(from = player.person.id, to = first().person.id, amount = BigDecimal.TEN) },
                        )
                    }
            val winner = first().copy(
                    bounties = players
                            .filterIndexed { index, _ -> index <= size }
                            .flatMap { it.bounties }
                            .filter { it.to == first().person.id },
            )
            return players + winner
        }
    }
}