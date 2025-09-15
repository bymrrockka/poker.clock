package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.scenario.UserCommand.Companion.createPoll
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PollScenario : AbstractScenarioTest() {

    @Test
    fun `create poll should trigger`(approver: Approver) {
        Given {
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * 3
                |message: Test poll
                |options: 
                |1. Yes - participant
                |2. Noooooo
                |3. Hell yeah 12123
                |4. ;.!@#$%^&*()(_+=<>.,/{}[]`~
                |5. I don't know
                """.trimMargin()
            }
            clock.set(clock.now() + 8.days)
            pollPosted()
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

}