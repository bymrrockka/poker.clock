package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.textApprover
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.createPoll
import by.mrrockka.scenario.UserCommand.Companion.stopPoll
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PollScenario : AbstractScenarioTest() {

    @Test
    fun `create and stop poll should send message`(approver: Approver) {
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
            message(replyTo = createPoll) { stopPoll }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "message, cron, options", "message, cron, element", "message, cron", "cron, options, element", "message, options, element"])
    fun `fail when doesn't have required fields`(actual: String) {
        Given {
            message {
                """
                |${createPoll}
                ${if (actual.contains("message")) "|message: Test poll" else ""}
                ${if (actual.contains("cron")) "|cron: * * * * * *" else ""}
                ${if (actual.contains("options")) "|options:" else ""}
                ${if (actual.contains("element")) "|Yes - participant" else ""}
            """.trimMargin()
            }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("fail when doesn't have required fields, field set ${if (actual.isNotBlank()) actual else "empty"}"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["/tournament_game", ""])
    fun `stop poll fail when wrong or no reply message specified`(command: String) {
        Given {
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * 3
                |message: Test poll
                |options: 
                |1. Yes - participant
                """.trimMargin()
            }
            message { "me".createGame(GameType.TOURNAMENT, 30.toBigDecimal()) }
            message(replyTo = command) { stopPoll }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("stop poll fail when ${if (command.isNotBlank()) "wrong" else "no"} reply message specified"))
    }

}