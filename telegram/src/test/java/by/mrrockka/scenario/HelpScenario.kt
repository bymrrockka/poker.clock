package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.scenario.UserCommand.Companion.help
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class HelpScenario : AbstractScenarioTest() {

    @Test
    fun `send help description if no command`(approver: Approver) {
        Given {
            message { help() }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `send command description by command name`(approver: Approver) {
        Given {
            message { help("tournament_game") }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `send command description by command alias`(approver: Approver) {
        Given {
            message { help("pp") }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }
}