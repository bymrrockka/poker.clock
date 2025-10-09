package by.mrrockka.executor

import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.repo.ChatPollsRepo
import by.mrrockka.repo.PinType
import by.mrrockka.service.PinMessageService
import by.mrrockka.service.PollEvent
import by.mrrockka.service.PollTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.types.msg.Message
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.DependsOn
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
@Component
@DependsOn("liquibase")
class TelegramTaskExecutor(
        private val bot: TelegramBot,
        private val pollService: PollTelegramService,
        private val chatPollsRepo: ChatPollsRepo,
        private val pinMessageService: PinMessageService,
        private val clock: Clock,
        private val transactionTemplate: TransactionTemplate,
) {
    @Volatile
    private var tasks: MutableMap<UUID, Task> = mutableMapOf()

    @PostConstruct
    fun init() {
        synchronized(tasks) {
            tasks = pollService.selectActive().asMap()
        }
    }

    @PreDestroy
    fun preDestroy() {
        synchronized(tasks) {
            pollService.batchUpdate(tasks.polls())
        }
    }

    @Scheduled(cron = "\${bot.scheduler.cron}")
    fun execute() {
        val now = clock.now().toJavaInstant()
        synchronized(tasks) {
            runBlocking {
                tasks.toExecute(now).forEach { task ->
                    transactionTemplate.execute {
                        async {
                            task.toAction()
                                    .sendReturning(to = task.chatId, bot)
                                    .onFailure { error("Failed to store poll id, game invitation poll wouldn't work") }
                                    .also { resp ->
                                        tasks[task.id] = task.updatedAt(now)
                                        val message = (resp as Message)

                                        chatPollsRepo.store(
                                                task.id,
                                                message.poll?.id ?: error("Poll message doesn't contain poll"),
                                        )

                                        pinMessageService.unpinAll(message, PinType.POLL)
                                        pinMessageService.pin(message, PinType.POLL)
                                    }
                        }
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
