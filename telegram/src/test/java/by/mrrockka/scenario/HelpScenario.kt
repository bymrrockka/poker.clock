package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.scenario.Commands.Companion.help
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class HelpScenario : AbstractScenarioTest() {

    @Test
    fun `send help description if no command`(approver: Approver) {
        Given {
            user { help() }
            bot { "Help message"}
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `send command description by command name`(approver: Approver) {
        Given {
            user { help("tournament_game") }
            bot { "Help message"}
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `send command description by command alias`(approver: Approver) {
        Given {
            user { help("pp") }
            bot { "Help message"}
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}