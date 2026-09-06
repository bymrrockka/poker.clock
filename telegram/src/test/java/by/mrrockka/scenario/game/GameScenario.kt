package by.mrrockka.scenario.game

import by.mrrockka.Command
import by.mrrockka.Given
import by.mrrockka.GivenSpecification
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.common.Commands.Companion.cancel
import by.mrrockka.scenario.common.Commands.Companion.entry
import by.mrrockka.scenario.common.Commands.Companion.game
import by.mrrockka.scenario.common.Commands.Companion.gameStats
import by.mrrockka.scenario.common.ScenarioTest
import by.mrrockka.scenario.common.createGameFlow
import by.mrrockka.service.up
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

abstract class GameScenario : ScenarioTest() {
    abstract fun gameType(): GameType

    @ParameterizedTest
    @ValueSource(ints = [1, 8, 18])
    fun `should generate randomized table seats when players enters the game (conversation)`(size: Int) {
        val buyin = BigDecimal(10)
        val players = (1..size).map { "nickname$it" }

        Given {
            createGameFlow(buyin, players)
            user("nickname1") { entry }
            bot { "entry stored" }
            user("nickname1") { entry }
            bot { "entry stored" }
            user("nickname${size + 1}") { entry }
            bot { "entry stored" }
            user("nickname${size + 2}") { entry }
            bot { "entry stored" }
            user("nickname${size + 3}") { entry }
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
            toDelete += user { buyin.up().toString() }
            toDelete += bot { "Question?" }
            toDelete += user { "cancel" }
            toDelete.deleted()
            toDelete += bot { "Was canceled" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should cancel entry on cancel command with attached message`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..4).map { "nickname$it" }

        Given {
            createGameFlow(buyin, players)
            user("nickname1") { entry }
            bot { "entry stored" }
            val entry = user("nickname1") { entry }
            bot { "entry stored" }
            user { gameStats }
            bot { "game stats" }
            user(username = "nickname1", replyTo = entry) { cancel }
            bot { "cancel command require admin permissions" }
            mainUser.isAdmin()
            user(replyTo = entry) { cancel }
            bot { "entry canceled" }
            user { gameStats }
            bot { "game stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    protected fun GivenSpecification.createGameFlow(buyin: BigDecimal, players: List<String>): Command.BotMessage = createGameFlow(gameType(), buyin, players)
}