package by.mrrockka.scenario.poll

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.Commands.Companion.chatPoll
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.createPoll
import by.mrrockka.scenario.Commands.Companion.stopPoll
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PollScenario : AbstractPollScenario() {

    @Test
    fun `create and stop poll should send message`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday
        clock.set(time)
        Given {
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * WED
                |message: Test poll
                |options: 
                |1. Yes - participant
                |2. Noooooo
                |3. Hell yeah 12123
                |4. ;.!@#$%^&*()(_+=<>.,/{}[]`~
                |5. I don't know
                """.trimMargin()
            }
            pollPosted(time + 8.days)
            chatPoll.pinned()
            message(replyTo = createPoll) { stopPoll }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `pinned posted poll becomes unpinned when new poll posted`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday
        clock.set(time)
        Given {
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * *
                |message: Test poll
                |options: 
                |1. Yes - participant
                |2. Noooooo
                |3. Hell yeah 12123
                |4. ;.!@#$%^&*()(_+=<>.,/{}[]`~
                |5. I don't know
                """.trimMargin()
            }
            pollPosted(time + 1.days)
            chatPoll.pinned()
            pollPosted(time + 2.days)
            chatPoll.pinned()
        } When {
            updatesReceived()
        } ThenApproveWith approver
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
        } ThenApproveWith mdApprover("fail when doesn't have required fields, field set ${if (actual.isNotBlank()) actual else "empty"}")
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
            message(replyTo = command) { stopPoll } // should fail
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("stop poll fail when ${if (command.isNotBlank()) "wrong" else "no"} reply message specified")
    }

}