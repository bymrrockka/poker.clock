package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.poll.PollTaskRepository
import by.mrrockka.validation.poll.PollMessageValidator
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.LocalDateTime
import java.util.*

@Service
class TaskTelegramService(
        private val pollMessageValidator: PollMessageValidator,
        private val pollMessageParser: PollMessageParser,
        private val pollTaskRepository: PollTaskRepository
) {
    private var tasks: Map<UUID, Task> = mapOf();

    @PostConstruct
    fun init() {
        assignTasks()
    }

    fun createPoll(messageMetadata: MessageMetadata): BotApiMethodMessage {
        val pollTask = messageMetadata.toPollTask()
        pollMessageValidator.validatePoll(pollTask)
        pollTaskRepository.upsert(pollTask)
        assignTasks()

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            replyToMessageId = messageMetadata.id
            text = """
                Poll created.
                Will be triggered at ${pollTask.cron.next(LocalDateTime.now())}
            """.trimIndent()
        }
    }

    fun getTasks(): List<Task> {
        return tasks.values.toList().orEmpty();
    }

    private fun assignTasks() {
        tasks = pollTaskRepository.selectNotFinished()
                .associateBy({ it.id }, { it })
                .orEmpty()
    }

    private fun MessageMetadata.toPollTask(): PollTask {
        return PollTask(
                id = UUID.randomUUID(),
                chatId = this.chatId,
                messageId = this.id,
                cron = pollMessageParser.parseCron(this),
                message = pollMessageParser.parseMessageText(this),
                options = pollMessageParser.parseOptions(this),
                createdAt = this.createdAt
        )
    }
}

