package by.mrrockka.scenario.poll

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.builder.person
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.Commands.Companion.chatPoll
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.createPoll
import by.mrrockka.scenario.Commands.Companion.stopPoll
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PollInvitationScenario : AbstractPollScenario() {

    @Test
    fun `create game based on poll answers`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday

        Given {
            clock.set(time)
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * WED
                |message: Test poll
                |options: 
                |1. Yes - participant
                |2. No
                |3. I don't know
                """.trimMargin()
            }
            pollPosted(time + 8.days)
            chatPoll.pinned()

            //participants
            listOf(person(), person()).forEach { person ->
                person.pollAnswer(1)
            }
            //no
            listOf(person(), person()).forEach { person ->
                person.pollAnswer(2)
            }
            //maybe
            person().pollAnswer(3)

            message(replyTo = chatPoll) {
                createGame(type = GameType.TOURNAMENT, BigDecimal(10))
            }
            message(replyTo = createPoll) { stopPoll }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail to create game when there are no participants`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday

        Given {
            clock.set(time)
            message {
                """
                |${createPoll}
                |cron: 0 0 0 * * WED
                |message: Test poll
                |options: 
                |1. Yes - participant
                |2. No
                |3. I don't know
                """.trimMargin()
            }
            pollPosted(time + 8.days)
            chatPoll.pinned()

            listOf(person(), person()).forEach { person ->
                person.pollAnswer(2)
            }
            person().pollAnswer(3)

            message(replyTo = chatPoll) {
                createGame(type = GameType.TOURNAMENT, BigDecimal(10))
            }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}