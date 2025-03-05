package by.mrrockka.scenario

import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.domain.GameType.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.stream.Stream

class GameScenario : AbstractScenarioTest() {

    @ParameterizedTest
    @MethodSource("createGameTypesArguments")
    fun `user sent command to create a game and receive successful message`(commandText: String, gameCreatedText: String, gameStatsText: String) {
        Given {
            command { message(commandText) }
            command { message("/game_stats") }
        } When {
            commands.updateReceived()
        } Then {
            expect<SendMessage> {
                url("/${SendMessage.PATH}}")
                result(SendMessageCreator.api {
                    it.text(gameCreatedText)
                })
            }
            expect<SendMessage> {
                url("/${SendMessage.PATH}}")
                result(SendMessageCreator.api {
                    it.text(gameStatsText)
                })
            }
        }
    }

    companion object {

        val games = mapOf(
                TOURNAMENT to """
                            /tournament_game
                            stack: 10k
                            buyin: 10
                            @nickname1
                            @Nickname2
                        """.trimIndent(),
                CASH to """
                            /cash_game
                            stack: 10k
                            buyin: 10
                            @nickname1
                        """.trimIndent(),
                BOUNTY to """
                            /bounty_game
                            stack: 10k
                            bounty: 10
                            buyin: 10
                            @nickname1
                            @nickASDame2
                            @nickname3
                            @nickname3
                        """.trimIndent()
        )


        @JvmStatic
        fun createGameTypesArguments(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(games[CASH], "Cash game started.", """
                        Game statistics:
                            - players entered -> 1
                            - total buy-in amount -> 10
                            - total withdrawal amount -> 0
                    """.trimIndent()),
                    Arguments.of(games[TOURNAMENT], "Tournament game started.", """
                        Game statistics:
                            - players entered -> 1
                            - number of entries -> 1
                            - total buy-in amount -> 10
                    """.trimIndent()),
                    Arguments.of(games[BOUNTY], "Bounty tournament game started.", """
                        Game statistics:
                            - players entered -> 1
                            - number of entries -> 1
                            - total buy-in amount -> 20
                            - bounties out of game -> 0
                    """.trimIndent()),
            )
        }
    }
}
