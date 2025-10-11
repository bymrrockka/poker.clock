package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entry
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

abstract class GameScenario : AbstractScenarioTest() {
    abstract fun gameType(): GameType

    @ParameterizedTest
    @ValueSource(ints = [1, 4, 8, 11, 15, 18])
    fun `should generate randomized table seats when players enters the game`(size: Int) {
        val buyin = BigDecimal(10)
        val players = (1..size).map { "nickname$it" }

        Given {
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { "nickname${size + 1}".entry() }
            message { "nickname${size + 2}".entry() }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should generate randomized table seats when players enters the game. Size $size")
    }
}