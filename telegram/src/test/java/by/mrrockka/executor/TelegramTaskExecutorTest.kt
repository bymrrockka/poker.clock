package by.mrrockka.executor

import by.mrrockka.bot.PokerClockAbsSender
import by.mrrockka.creator.TaskCreator
import by.mrrockka.service.TaskCreated
import by.mrrockka.service.TaskTelegramService
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant

@ExtendWith(MockKExtension::class)
class TelegramTaskExecutorTest {

    @MockK(relaxed = true)
    lateinit var taskTelegramService: TaskTelegramService

    @MockK(relaxed = true)
    lateinit var pokerClockAbsSender: PokerClockAbsSender

    @InjectMockKs
    lateinit var telegramTaskExecutor: TelegramTaskExecutor

    @AfterEach
    fun afterEach() {
        confirmVerified(taskTelegramService, pokerClockAbsSender)
    }

    @Test
    fun `given empty tasks list when execute triggered should not send anything`() {
        every { taskTelegramService.getTasks() } returns listOf()

        telegramTaskExecutor.init()
        verify { taskTelegramService.getTasks() }

        telegramTaskExecutor.execute()
        verify { pokerClockAbsSender wasNot Called }
    }

    @Test
    fun `given applicable tasks list when execute triggered should send message`() {
        val tasks = listOf(
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1))),
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1))),
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1)))
        )
        every { taskTelegramService.getTasks() } returns tasks

        telegramTaskExecutor.init()
        verify { taskTelegramService.getTasks() }

        telegramTaskExecutor.execute()
        tasks.forEach {
            verify { pokerClockAbsSender.executeAsync(it.toMessage()) }
        }
    }

    @Test
    fun `given tasks list when task updated event consumed should update tasks`() {
        val initTasks = listOf(
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1)))
        )
        val updatedTask = TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1)))
        every { taskTelegramService.getTasks() } returns initTasks

        telegramTaskExecutor.init()
        verify { taskTelegramService.getTasks() }

        telegramTaskExecutor.taskCreated(TaskCreated(updatedTask))
        telegramTaskExecutor.execute()
        (initTasks + updatedTask).forEach {
            verify { pokerClockAbsSender.executeAsync(it.toMessage()) }
        }

    }

    @Test
    fun `given tasks list when bean destroy executed should store updated tasks`() {
        val tasks = listOf(
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1))),
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1))),
                TaskCreator.randomPoll().copy(createdAt = Instant.now().minus(Duration.ofHours(1)))
        )
        val tasksMap = tasks.map { it.id to it }.toMap()
        every { taskTelegramService.getTasks() } returns tasks

        telegramTaskExecutor.init()
        verify { taskTelegramService.getTasks() }

        telegramTaskExecutor.execute()
        tasks.forEach {
            verify { pokerClockAbsSender.executeAsync(it.toMessage()) }
        }

        telegramTaskExecutor.preDestroy()
        verify {
            taskTelegramService.batchUpdate(match { list ->
                !list.map { it.updatedAt != null && tasksMap[it.id] == it.copy(updatedAt = null) }.contains(false)
            })
        }
    }

}