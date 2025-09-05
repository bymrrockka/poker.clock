package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.textApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class GameScenario : AbstractScenarioTest() {
    @ParameterizedTest
    @MethodSource("games")
    fun `user sent command to create a game and receive successful message`(gameType: GameType, commandText: String) {
        Given {
            command { message(commandText) }
            command { message(UserCommand.gameStats) }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("game ${gameType.name} creation log"))
    }

    companion object {
        @JvmStatic
        fun games(): Stream<Arguments> = mapOf(
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
        ).map { (key, value) ->
            Arguments.of(key, value)
        }.run { Stream.of(*this.toTypedArray()) }
    }
}