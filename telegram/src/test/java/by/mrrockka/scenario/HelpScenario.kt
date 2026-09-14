package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.scenario.common.Commands.Companion.help
import by.mrrockka.scenario.common.ScenarioTest
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class HelpScenario : ScenarioTest() {

    @Test
    fun `send help description if no command`(approver: Approver) {
        Given {
            user { help() }
            bot { "Help message" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `send command description by command name`(approver: Approver) {
        Given {
            user { help("game") }
            bot { "Help message" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `send command description by command alias`(approver: Approver) {
        Given {
            user { help("prize_pool") }
            bot { "Help message" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}