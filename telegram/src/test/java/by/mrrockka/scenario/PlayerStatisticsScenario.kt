package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.entry
import by.mrrockka.scenario.UserCommand.Companion.kicked
import by.mrrockka.scenario.UserCommand.Companion.playerStats
import by.mrrockka.scenario.UserCommand.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PlayerStatisticsScenario : AbstractScenarioTest() {

    @Test
    fun `player statistics in tournament game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { playerStats }
            message { entry() }
            message { playerStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `player statistics in cash game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            message { players.createGame(GameType.CASH, buyin) }
            message { playerStats }
            message { entry(30) }
            message { playerStats }
            message { "me".withdrawal(30) }
            message { playerStats }
            message { "me".withdrawal(20) }
            message { playerStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `player statistics in bounty game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1",
        )
        Given {
            message { players.createGame(GameType.BOUNTY, buyin) }
            message { playerStats }
            message { "me" kicked "nickname1" }
            message { playerStats }
            message { "nickname1".entry() }
            message { "nickname1" kicked "me" }
            message { playerStats }
            message { "me".entry() }
            message { playerStats }
            message { "nickname1" kicked "me" }
            message { playerStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

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