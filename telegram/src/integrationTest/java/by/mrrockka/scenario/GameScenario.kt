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
    fun `user sent command to create a game and receive successful message`(commandText: String, expectedMessage: String) {
        Given {
            command { it.message(commandText) }
            command { it.message("/game_stats") }
        } When {
            commands.updateReceived()
        } Then {
            expect<SendMessage> {
                it.url = "/${SendMessage.PATH}}"
                it.result = SendMessageCreator.api {
                    it.text(expectedMessage)
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("createGameTypesArguments")
    fun `given a game when new player enters the game then should add and sent message`(commandText: String, expectedMessage: String) {
        Given {
            command { it.message(commandText) }
        } When {
            commands.updateReceived()
        } Then {
            expect<SendMessage> {
                it.url = "/${SendMessage.PATH}}"
                it.result = SendMessageCreator.api {
                    it.text(expectedMessage)
                }
            }
        }
    }

    companion object {

        val games = mapOf(
                TOURNAMENT to """
                            /tournament_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                            @Nickname2
                        """.trimIndent(),
                CASH to """
                            /cash_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                        """.trimIndent(),
                BOUNTY to """
                            /bounty_game
                            stack: 30k
                            bounty: 30
                            buyin: 30
                            @nickname1
                            @nickASDame2
                            @nickname3
                            @nickname3
                        """.trimIndent()
        )


        @JvmStatic
        fun createGameTypesArguments(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(games[CASH], "Cash game started."),
                    Arguments.of(games[TOURNAMENT], "Tournament started."),
                    Arguments.of(games[BOUNTY], "Bounty tournament started."),
            )
        }
    }
}
