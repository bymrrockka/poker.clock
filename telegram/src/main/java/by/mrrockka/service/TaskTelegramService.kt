package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.repo.poll.PollTaskRepository
import by.mrrockka.validation.poll.PollMessageValidator
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.*

@Service
class TaskTelegramService(
    private val pollMessageValidator: PollMessageValidator,
    private val pollTaskRepository: PollTaskRepository
) {
    var tasks: Map<UUID, Task> = mapOf();

    @PostConstruct
    fun init() {
        assignTasks()
    }

    fun createPoll(messageMetadata: MessageMetadata): BotApiMethodMessage {
        return transaction {
            pollMessageValidator.validatePoll(messageMetadata.toPollTask())
            pollTaskRepository.upsert(messageMetadata.toPollTask())
            assignTasks()

            SendMessage().apply {
                chatId = messageMetadata.chatId.toString()
                replyToMessageId = messageMetadata.id
                text = """
                Poll created.  
                Will be triggered at $
            """.trimIndent()
            }
        }
    }

    fun getTasks(): List<Task> {
        return tasks.values.toList().orEmpty();
    }

    private fun assignTasks() {
        tasks = pollTaskRepository.selectNotFinished()
            .associateBy({ it.id }, { it })
    }
}

private fun MessageMetadata.toPollTask(): PollTask {
    return PollTask(
        id = UUID.randomUUID(),
        chatId = this.chatId,
        messageId = this.id,
        cron = this.text,
        message = this.text,
        options = listOf(),
        createdAt = this.createdAt
    )
}