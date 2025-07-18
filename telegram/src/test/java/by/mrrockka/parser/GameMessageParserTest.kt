package by.mrrockka.parser

import by.mrrockka.builder.message
import by.mrrockka.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

internal class GameMessageParserTest {
    private val gameMessageParser = GameMessageParser()

    @ParameterizedTest
    @MethodSource("gameMessages")
    fun `valid message text should create game`(message: MessageMetadata, block: (Game) -> Unit) {
        assertDoesNotThrow { gameMessageParser.parse(message).let(block) }
    }

    @ParameterizedTest
    @MethodSource("invalidGameMessages")
    fun `message with missed params should throw exception`(message: MessageMetadata) {
        assertThrows<IllegalStateException> { gameMessageParser.parse(message) }
    }

    companion object {
        @JvmStatic
        private fun gameMessages() = Stream.of(
                Arguments.of(
                        message {
                            text = """
                          /tournament_game 
                          buyin: 30  
                          stack: 30.5k 
                          players: 
                            @mrrockka
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(TournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("30"))
                            assertThat(game.stack).isEqualTo(BigDecimal("30500"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /tournament_game 
                          buyin:    15   
                            @mrrockka
                          @ivano 
                           @andrei 
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(TournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("15"))
                            assertThat(game.stack).isEqualTo(BigDecimal.ZERO)
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /tournament_game
                          buyin:      100
                          stack:50000
                          players:
                            @mrrockka
                          @me
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(TournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("100"))
                            assertThat(game.stack).isEqualTo(BigDecimal("50000"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /bounty_game 
                          buyin: 30  
                          stack: 30k 
                          bounty:30
                          players: 
                            @mrrockka
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(BountyTournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("30"))
                            assertThat(game.stack).isEqualTo(BigDecimal("30000"))
                            assertThat((game as BountyTournamentGame).bounty).isEqualTo(BigDecimal("30"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /bounty_game 
                          buyin:    15k  
                          bounty:300 
                          stack: 1.5k
                            @mrrockka
                          @ivano 
                           @andrei 
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(BountyTournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("15000"))
                            assertThat(game.stack).isEqualTo(BigDecimal("1500"))
                            assertThat((game as BountyTournamentGame).bounty).isEqualTo(BigDecimal("300"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /bounty_game
                          bounty:30
                          buyin:      10K
                          players:
                            @mrrockka
                          @me
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(BountyTournamentGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("10000"))
                            assertThat(game.stack).isEqualTo(BigDecimal.ZERO)
                            assertThat((game as BountyTournamentGame).bounty).isEqualTo(BigDecimal("30"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /cash_game 
                          buyin: 30  
                          stack: 30k 
                          players: 
                            @mrrockka
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(CashGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("30"))
                            assertThat(game.stack).isEqualTo(BigDecimal("30000"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /cash_game 
                          buyin:    15   
                          bounty:30 
                          stack: 1.5k
                            @mrrockka
                          @ivano 
                           @andrei 
                          @ivano 
                           @andrei 
                          @me   
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(CashGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("15"))
                            assertThat(game.stack).isEqualTo(BigDecimal("1500"))
                        },
                ),

                Arguments.of(
                        message {
                            text = """
                          /cash_game
                          buyin:      100
                          players:
                            @mrrockka
                          @me
                          """.trimIndent()
                        },
                        { game: Game ->
                            assertThat(game).isInstanceOf(CashGame::class.java)
                            assertThat(game.buyIn).isEqualTo(BigDecimal("100"))
                            assertThat(game.stack).isEqualTo(BigDecimal.ZERO)
                        },
                ),
        )

        @JvmStatic
        private fun invalidGameMessages() = listOf(
                message {
                    text = """
                    /tournament_game   
                    stack: 1.5k 
                    players: 
                      @mrrockka
                      @me
                    """.trimIndent()
                },

                message {
                    text = """
                    /bounty_game
                    buyin:    15  
                    players: 
                      @mrrockka
                      @me
                    """.trimIndent()
                },
        )
    }

}
