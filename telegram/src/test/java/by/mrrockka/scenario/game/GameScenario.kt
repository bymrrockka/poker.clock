package by.mrrockka.scenario.game

import by.mrrockka.Command
import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entries
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.game
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

abstract class GameScenario : AbstractScenarioTest() {
    abstract fun gameType(): GameType

    @ParameterizedTest
    @ValueSource(ints = [1, 8, 18])
    fun `should generate randomized table seats when players enters the game`(size: Int) {
        val buyin = BigDecimal(10)
        val players = (1..size).map { "nickname$it" }

        Given {
            message { players.createGame(gameType(), buyin) }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { "nickname${size + 1}".entry() }
            message { "nickname${size + 2}".entry() }
            message { "nickname${size + 3}".entry() }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should generate randomized table seats when players enters the ${gameType()} game. Size $size")
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 8, 18])
    fun `should generate randomized table seats when players enters the game (conversation)`(size: Int) {
        val buyin = BigDecimal(10)
        val players = (1..size).map { "nickname$it" }
        val toDelete = mutableListOf<Command.Message>()

        Given {
            message { game }
            //set game type
            toDelete += message { gameType().title }
            //set buyin
            toDelete += message { buyin.setScale(0).toString() }
            if(gameType() == GameType.BOUNTY) {
                //set bounty
                toDelete += message { buyin.setScale(0).toString() }
            }
            //set players
            toDelete += message { players.entries() }
            toDelete.deleted()
            //entries to game
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { "nickname${size + 1}".entry() }
            message { "nickname${size + 2}".entry() }
            message { "nickname${size + 3}".entry() }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should generate randomized table seats when players enters the ${gameType()} game (conversation). Size $size")
    }
}