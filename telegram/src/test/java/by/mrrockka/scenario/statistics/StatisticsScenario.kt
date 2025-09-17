package by.mrrockka.scenario.statistics

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.gameStats
import by.mrrockka.scenario.Commands.Companion.kicked
import by.mrrockka.scenario.Commands.Companion.playerStats
import by.mrrockka.scenario.Commands.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

abstract class StatisticsScenario : AbstractScenarioTest() {
    abstract val statisticsCommand: String

    @Test
    fun `statistics in tournament game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { statisticsCommand }
            message { entry() }
            message { statisticsCommand }
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
            message { players.createGame(GameType.CASH, buyin) }
            message { statisticsCommand }
            message { entry(30) }
            message { statisticsCommand }
            message { "me".withdrawal(30) }
            message { statisticsCommand }
            message { "me".withdrawal(20) }
            message { statisticsCommand }
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
            message { players.createGame(GameType.BOUNTY, buyin) }
            message { statisticsCommand }
            message { "me" kicked "nickname1" }
            message { statisticsCommand }
            message { "nickname1".entry() }
            message { "nickname1" kicked "me" }
            message { statisticsCommand }
            message { "me".entry() }
            message { statisticsCommand }
            message { "nickname1" kicked "me" }
            message { statisticsCommand }
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
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { playerStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}
