package by.mrrockka.scenario.game

import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.UserCommand
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.withdrawal
import by.mrrockka.scenario.When
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class CashGameScenario : AbstractScenarioTest() {
    @Test
    fun `given cash game when all money withdrawn then should be able to calculate`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            command { message(players.createGame(GameType.CASH, buyin)) }
            command { message(players[0].withdrawal(20)) }
            command { message(UserCommand.calculate) }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

}
