package by.mrrockka.executor

import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.service.PollEvent
import by.mrrockka.service.PollTelegramService
import eu.vendeli.tgbot.TelegramBot
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
@Component
class TelegramTaskExecutor(
        private val taskTelegramService: PollTelegramService,
        private val bot: TelegramBot,
        private val clock: Clock,
) {
    @Volatile
    private var tasks: MutableMap<UUID, Task> = mutableMapOf()

    fun init() {
        if (tasks.isEmpty()) {
            synchronized(tasks) {
                tasks = taskTelegramService.selectActive().asMap()
            }
        }
    }

    @PreDestroy
    fun preDestroy() {
        synchronized(tasks) {
            taskTelegramService.batchUpdate(tasks.polls())
        }
    }

    @Scheduled(cron = "\${bot.scheduler.cron}")
    fun execute() {
        init()
        val now = clock.now().toJavaInstant()
        synchronized(tasks) {
            tasks.toExecute(now)
                    .forEach { task ->
                        runBlocking {
                            async {
                                task.toMessage().send(to = task.chatId, bot)
                                tasks[task.id] = task.updatedAt(now)
                            }
                        }
                    }
        }
    }

    @EventListener
    fun pollTaskCreated(event: PollEvent.Created) {
        synchronized(tasks) {
            tasks += event.task.id to event.task
        }
    }

    @EventListener
    fun pollTaskFinished(event: PollEvent.Finished) {
        synchronized(tasks) {
            tasks.polls().first {
                it.messageId == event.messageId
            }.let {
                tasks[it.id] = it.copy(
                        finishedAt = event.finishedAt,
                        updatedAt = clock.now().toJavaInstant(),
                )
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
            else -> error("Unknown task type")
        }
    }
}
