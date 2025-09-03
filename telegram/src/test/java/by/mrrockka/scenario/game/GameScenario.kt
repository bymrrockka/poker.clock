package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.textApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class GameScenario : AbstractScenarioTest() {
    @ParameterizedTest
    @MethodSource("games")
    fun `user sent command to create a game and receive successful message`(gameType: GameType, commandText: String, approver: Approver) {
        Given {
            command { message(commandText) }
            command { message(UserCommand.gameStats) }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("game ${gameType.name} creation log"))
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
        fun games(): Stream<Arguments> = games.map { (key, value) ->
            Arguments.of(key, value)
        }.run { Stream.of(*this.toTypedArray()) }

        @JvmStatic
        fun createGameTypesArguments(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(
                            GameType.CASH,
                            games[GameType.CASH],
                    ),
                    Arguments.of(
                            GameType.TOURNAMENT,
                            games[GameType.TOURNAMENT],
                    ),
                    Arguments.of(
                            GameType.BOUNTY,
                            games[GameType.BOUNTY],
                    ),
            )
        }
    }
}