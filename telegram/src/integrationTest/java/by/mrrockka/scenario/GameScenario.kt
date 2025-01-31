package by.mrrockka.scenario

import by.mrrockka.creator.SendMessageCreator
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.EntityType.BOTCOMMAND
import org.telegram.telegrambots.meta.api.objects.EntityType.MENTION
import java.util.stream.Stream

class GameScenario : AbstractScenarioTest() {

    @ParameterizedTest
    @MethodSource("createGameTypesArguments")
    fun `user sent command to create a game and receive successful message`(commandText: String, expectedMessage: String) {
        Given {
            command { it.message(commandText) }
        } When {
            it.command.updateReceived()
        } Then {
            it.url = "/${SendMessage.PATH}}"
            it.result = SendMessageCreator.api {
                it.text(expectedMessage)
            }
        }
    }

    companion object {
        @JvmStatic
        fun createGameTypesArguments(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(
                            """
                            /cash_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                            @nickname2
                            @nickname3
                        """.trimIndent(),
                            "Cash game started."
                    ),
                    Arguments.of(
                            """
                            /tournament_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                            @nickname2
                            @nickname3
                        """.trimIndent(),
                            "Tournament started."
                    ),
                    Arguments.of(
                            """
                            /bounty_game
                            stack: 30k
                            bounty: 30
                            buyin: 30
                            @nickname1
                            @nickname2
                            @nickname3
                        """.trimIndent(),
                            "Bounty tournament started."
                    ),
            )
        }
    }
}
