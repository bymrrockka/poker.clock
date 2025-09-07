package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.entry
import by.mrrockka.scenario.UserCommand.Companion.prizePool
import by.mrrockka.scenario.UserCommand.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class CashGameScenario : AbstractScenarioTest() {
    @Test
    fun `should calculate when all money were withdraw`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )

        Given {
            command { players.createGame(GameType.CASH, buyin) }
            command { players[0].withdrawal(20) }
            command { players[1].withdrawal(30) }
            command { players[3].entry(20) }
            command { players[2].withdrawal(30) }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `should create game with one player and calculate when other entries`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player1 = "nickname1"
        val player2 = "nickname2"

        Given {
            command { listOf(player1).createGame(GameType.CASH, buyin) }
            command { player2.entry() }
            command { player1.withdrawal(20) }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }
    @Test
    fun `should send error when calculation started but there are still money in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            command { players.createGame(GameType.CASH, buyin) }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `should send error when withdrawal is more then money left in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            command { players.createGame(GameType.CASH, buyin) }
            command { players[0].withdrawal(40) }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `should send error when prize pool added`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            command { players.createGame(GameType.CASH, buyin) }
            command { prizePool(1) }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }
}
