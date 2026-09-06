package by.mrrockka.scenario.statistics

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.common.Commands.Companion.createGame
import by.mrrockka.scenario.common.Commands.Companion.entry
import by.mrrockka.scenario.common.Commands.Companion.gameStats
import by.mrrockka.scenario.common.Commands.Companion.kicked
import by.mrrockka.scenario.common.Commands.Companion.playerStats
import by.mrrockka.scenario.common.Commands.Companion.withdrawal
import by.mrrockka.scenario.common.ScenarioTest
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

abstract class StatisticsScenario : ScenarioTest() {
    abstract val statisticsCommand: String

    @Test
    fun `statistics in tournament game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            bot { "Game created" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { entry }
            bot { "Entry stored" }
            user { statisticsCommand }
            bot { "Players stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `statistics in cash game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            user { players.createGame(GameType.CASH, buyin) }
            bot { "Players stats" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { entry(30) }
            bot { "Entry stored" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { withdrawal(30) }
            bot { "Withdraw" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { withdrawal(20) }
            bot { "Withdraw" }
            user { statisticsCommand }
            bot { "Players stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `statistics in bounty game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            user { players.createGame(GameType.BOUNTY, buyin) }
            bot { "Game created" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { "me" kicked "nickname1" }
            bot { "Player kicked" }
            user { statisticsCommand }
            bot { "Players stats" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            user { "nickname1" kicked "me" }
            bot { "Player kicked" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { entry }
            bot { "Entry stored" }
            user { statisticsCommand }
            bot { "Players stats" }
            user { "nickname1" kicked "me" }
            bot { "Player kicked" }
            user { statisticsCommand }
            bot { "Players stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}

class GameStatisticsScenario : StatisticsScenario() {
    override val statisticsCommand: String = gameStats
}

class PlayerStatisticsScenario : StatisticsScenario() {
    override val statisticsCommand: String = playerStats

    @Test
    fun `fail when nickname is not in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "nickname2", "nickname1",
        )
        Given {
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            bot { "Game created" }
            user { playerStats }
            bot { "Players stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}
