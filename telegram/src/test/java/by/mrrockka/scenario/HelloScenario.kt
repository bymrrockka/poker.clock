package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class HelloScenario : AbstractScenarioTest() {
    @Test
    fun `say hello`(approver: Approver) {
        Given {
            command { message("/hello") }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }
}