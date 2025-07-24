package by.mrrockka.scenario.game

import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.UserCommand
import by.mrrockka.scenario.When
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
            command { message(UserCommand.gameStats) }
        } When {
            updatesReceived()
        } Then {
            expect { text<SendMessage>(gameCreatedText) }
            expect { text<SendMessage>(gameStatsText) }
        }
    }

    companion object {
        val games = mapOf(
                GameType.TOURNAMENT to """
                            ${UserCommand.tournamentGame}
                            stack: 10k
                            buyin: 10
                            @nickname1
                            @Nickname2
                        """.trimIndent(),
                GameType.CASH to """
                            ${UserCommand.cashGame}
                            stack: 10k
                            buyin: 10
                            @nickname1
                        """.trimIndent(),
                GameType.BOUNTY to """
                            ${UserCommand.bountyGame}
                            stack: 10k
                            bounty: 10
                            buyin: 10
                            @nickname1
                            @nickASDame2
                            @nickname3
                            @nickname3
                        """.trimIndent(),
        )

        @JvmStatic
        fun createGameTypesArguments(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(
                            games[GameType.CASH], "Cash game started.",
                            """
                        Cash game statistics:
                            - players entered -> 1
                            - total buy-in amount -> 10
                            - total withdrawal amount -> 0
                    """.trimIndent(),
                    ),
                    Arguments.of(
                            games[GameType.TOURNAMENT], "Tournament game started.",
                            """
                        Tournament game statistics:
                            - players entered -> 2
                            - number of entries -> 2
                            - total buy-in amount -> 20
                    """.trimIndent(),
                    ),
                    Arguments.of(
                            games[GameType.BOUNTY], "Bounty tournament game started.",
                            """
                        Bounty game statistics:
                            - players entered -> 3
                            - number of entries -> 3
                            - total buy-in amount -> 60
                            - bounties out of game -> 0
                    """.trimIndent(),
                    ),
            )
        }
    }
}