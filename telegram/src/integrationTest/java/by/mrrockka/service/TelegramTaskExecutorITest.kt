package by.mrrockka.service

import by.mrrockka.bot.PokerClockAbsSender
import by.mrrockka.extension.TelegramPSQLExtension
import by.mrrockka.creator.TaskCreator
import by.mrrockka.executor.TelegramTaskExecutor
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.SpykBean
import io.mockk.confirmVerified
import io.mockk.verify
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.Instant

@ExtendWith(TelegramPSQLExtension::class)
@SpringBootTest
@ActiveProfiles("repository")
class TelegramTaskExecutorITest {

    @MockkBean(relaxed = true, clear = MockkClear.BEFORE)
    lateinit var pokerClockAbsSender: PokerClockAbsSender

    @SpykBean(clear = MockkClear.BEFORE)
    lateinit var taskTelegramService: TaskTelegramService

    @Autowired
    lateinit var taskExecutor: TelegramTaskExecutor

    @Autowired
    lateinit var eventPublisher: ApplicationEventPublisher

    val tasks = listOf(
            TaskCreator.randomPoll(),
            TaskCreator.randomPoll()
    );

    @BeforeEach
    fun before() {
        taskExecutor.init()
        verify { taskTelegramService.getTasks() }
    }

    @AfterEach
    fun after() {
        confirmVerified(taskTelegramService, pokerClockAbsSender)
    }

    @Test
    fun `given task list on execute should send messages`() {
        tasks.forEach {
            eventPublisher.publishEvent(PollTaskCreated(it))
        }

        val newTask = TaskCreator.randomPoll()
        eventPublisher.publishEvent(PollTaskCreated(newTask))

        await atMost Duration.ofMillis(2000L) untilAsserted {
            (tasks + newTask).forEach {
                verify { pokerClockAbsSender.executeAsync(it.toMessage()) }
            }
        }
    }

    @Test
    fun `given task list when task finished should filter it out`() {
        tasks.forEach {
            eventPublisher.publishEvent(PollTaskCreated(it))
        }
        eventPublisher.publishEvent(PollTaskFinished(tasks[0].messageId, Instant.now().minusMillis(1000L)))
        await atMost Duration.ofMillis(2000L) untilAsserted {
            verify { pokerClockAbsSender.executeAsync(tasks[1].toMessage()) }
        }
    }
}