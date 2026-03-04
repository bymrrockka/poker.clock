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
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            user { statisticsCommand }
            user { entry() }
            user { statisticsCommand }
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
            user { statisticsCommand }
            user { entry(30) }
            user { statisticsCommand }
            user { "me".withdrawal(30) }
            user { statisticsCommand }
            user { "me".withdrawal(20) }
            user { statisticsCommand }
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
            user { statisticsCommand }
            user { "me" kicked "nickname1" }
            user { statisticsCommand }
            user { "nickname1".entry() }
            user { "nickname1" kicked "me" }
            user { statisticsCommand }
            user { "me".entry() }
            user { statisticsCommand }
            user { "nickname1" kicked "me" }
            user { statisticsCommand }
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
            user { playerStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}
