package by.mrrockka.executor

import by.mrrockka.bot.PokerClockAbsSender
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.exception.BusinessException
import by.mrrockka.service.PollTaskCreated
import by.mrrockka.service.PollTaskFinished
import by.mrrockka.service.TaskTelegramService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import kotlin.concurrent.Volatile

@Component
class TelegramTaskExecutor(
        private val taskTelegramService: TaskTelegramService,
        private val pokerClockAbsSender: PokerClockAbsSender
) {
    @Volatile
    private var tasks: MutableMap<UUID, Task> = mutableMapOf()

    @PostConstruct
    fun init() {
        tasks = taskTelegramService.getTasks().asMap()
    }

    @PreDestroy
    fun preDestroy() {
        synchronized(tasks) {
            taskTelegramService.batchUpdate(tasks.polls())
        }
    }

    @Scheduled(fixedRate = 1000L)
    fun execute() {
        val now = Instant.now()
        synchronized(tasks) {
            tasks.toExecute(now)
                    .forEach { task ->
                        pokerClockAbsSender.executeAsync(task.toMessage())
                        tasks[task.id] = task.updatedAt(now)
                    }
        }
    }

    @EventListener
    fun pollTaskCreated(event: PollTaskCreated) {
        synchronized(tasks) {
            tasks += event.task.id to event.task
        }
    }

    @EventListener
    fun pollTaskFinished(event: PollTaskFinished) {
        synchronized(tasks) {
            tasks.polls().filter {
                it.messageId == event.messageId
            }.first().let {
                tasks[it.id] = it.copy(finishedAt = event.finishedAt)
            }
        }
    }

    private fun List<Task>.asMap(): MutableMap<UUID, Task> = associate { it.id to it } as MutableMap<UUID, Task>

    private fun Map<UUID, Task>.toExecute(now: Instant): List<Task> = values.filter { it.shouldBeExecuted(now) }

    private fun Map<UUID, Task>.polls(): List<PollTask> = values
            .filter { it is PollTask }
            .map { it as PollTask }
            .toList()

    private fun Task.updatedAt(time: Instant): Task {
        return when (this) {
            is PollTask -> copy(updatedAt = time)
            else -> throw UnknownTaskException()
        }
    }
}

class UnknownTaskException : BusinessException("Unknown task type")