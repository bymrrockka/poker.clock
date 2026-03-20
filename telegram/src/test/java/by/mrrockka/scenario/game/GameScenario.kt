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
import by.mrrockka.service.up
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
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
            user { players.createGame(gameType(), buyin) }
            bot { "game created" }
            user { "nickname1".entry() }
            bot { "entry stored" }
            user { "nickname1".entry() }
            bot { "entry stored" }
            user { "nickname${size + 1}".entry() }
            bot { "entry stored" }
            user { "nickname${size + 2}".entry() }
            bot { "entry stored" }
            user { "nickname${size + 3}".entry() }
            bot { "entry stored" }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should generate randomized table seats when players enters the ${gameType()} game. Size $size")
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 8, 18])
    fun `should generate randomized table seats when players enters the game (conversation)`(size: Int) {
        val buyin = BigDecimal(10)
        val players = (1..size).map { "nickname$it" }
        val toDelete = mutableListOf<Command>()

        Given {
            user { game }
            toDelete += bot { "Type of game?" }
            toDelete += user { gameType().title }
            toDelete += bot { "Buyin?" }
            toDelete += user { buyin.up().toString() }
            if (gameType() == GameType.BOUNTY) {
                toDelete += bot { "Bounty?" }
                toDelete += user { buyin.up().toString() }
            }
            toDelete += bot { "Players?" }
            toDelete += user { players.entries() }
            val game = bot { "Game created" }
            game.pinned()
            toDelete.deleted()
            user { "nickname1".entry() }
            bot { "entry stored" }
            user { "nickname1".entry() }
            bot { "entry stored" }
            user { "nickname${size + 1}".entry() }
            bot { "entry stored" }
            user { "nickname${size + 2}".entry() }
            bot { "entry stored" }
            user { "nickname${size + 3}".entry() }
            bot { "entry stored" }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should generate randomized table seats when players enters the ${gameType()} game (conversation). Size $size")
    }

    @Test
    fun `should cancel game creation when 'cancel' input`(approver: Approver) {
        val buyin = BigDecimal(10)
        val toDelete = mutableListOf<Command>()

        Given {
            user { game }
            toDelete += bot { "Type of game?" }
            toDelete += user { gameType().title }
            toDelete += bot { "Buyin?" }
            toDelete += user { buyin.setScale(0).toString() }
            toDelete += bot { "Question?" }
            toDelete += user { "cancel" }
            toDelete += bot { "Was canceled" }
            toDelete.deleted()
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}