package by.mrrockka.scenario.poll

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.builder.person
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.common.Commands.Companion.createPoll
import by.mrrockka.scenario.common.Commands.Companion.game
import by.mrrockka.scenario.common.Commands.Companion.gameStats
import by.mrrockka.scenario.common.Commands.Companion.stopPoll
import by.mrrockka.scenario.common.createGameFlow
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
            val createPoll = user {
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
            bot { "Poll will be triggered" }
            val poll = pollPosted(time + 8.days)
            poll.pinned()

            //participants
            listOf(person(), person()).forEach { person ->
                poll.pollAnswer(person, 1)
            }
            //no
            listOf(person(), person()).forEach { person ->
                poll.pollAnswer(person, 2)
            }
            //maybe
            poll.pollAnswer(person(), 3)

            createGameFlow(GameType.TOURNAMENT, BigDecimal(10), replyTo = poll)
            user { gameStats }
            bot { "Game stats" }
            user(replyTo = createPoll) { stopPoll }
            bot { "Poll stoped" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `exclude persons entries when game created by invitation poll and has mentions`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday
        val participants = listOf(person(), person())
        Given {
            clock.set(time)
            user {
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
            bot { "Poll will be triggered" }
            val poll = pollPosted(time + 8.days)
            //participants
            participants.forEach { person ->
                poll.pollAnswer(person, 1)
            }
            //no
            listOf(person(), person()).forEach { person ->
                poll.pollAnswer(person, 2)
            }
            //maybe
            poll.pollAnswer(person(), 3)
            createGameFlow(GameType.TOURNAMENT, BigDecimal(10), players = participants.drop(1).map { it.nickname!! }, replyTo = poll)
            user { gameStats }
            bot { "Game stats" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail to create game when there are no participants`(approver: Approver) {
        val time = Instant.parse("2025-09-16T12:34:56Z") //Tuesday

        Given {
            clock.set(time)
            user {
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
            bot { "Poll will be triggered" }
            val poll = pollPosted(time + 8.days)
            poll.pinned()

            listOf(person(), person()).forEach { person ->
                poll.pollAnswer(person, 2)
            }
            poll.pollAnswer(person(), 3)

            user { game }
            bot { "Type of game?" }
            user { GameType.TOURNAMENT.title }
            bot { "Buyin?" }
            user { BigDecimal(10).toString() }
            bot { "Players?" }
            user(replyTo = poll) { "." }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}