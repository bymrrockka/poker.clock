package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.exception.BusinessException
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.poll.PollTaskRepository
import by.mrrockka.validation.poll.PollMessageValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class TaskTelegramService(
        private val pollMessageValidator: PollMessageValidator,
        private val pollMessageParser: PollMessageParser,
        private val pollTaskRepository: PollTaskRepository,
        private val eventPublisher: ApplicationEventPublisher
) {

    fun createPoll(messageMetadata: MessageMetadata): BotApiMethodMessage {
        val pollTask = messageMetadata.toPollTask()
        pollMessageValidator.validatePoll(pollTask)
        pollTaskRepository.upsert(pollTask)
        eventPublisher.publishEvent(PollTaskCreated(pollTask))

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            replyToMessageId = messageMetadata.id
            text = """
                Poll created.
                Will be triggered at ${pollTask.cron.next(LocalDateTime.now())}
            """.trimIndent()
        }
    }

    fun stopPoll(messageMetadata: MessageMetadata): BotApiMethodMessage {
        if (messageMetadata.replyTo == null) throw NoAttachedMessagesFound()
        val size = pollTaskRepository.finishPoll(
                messageMetadata.replyTo.id,
                messageMetadata.createdAt
        )
        if (size < 1) throw NoPollsFound()

        eventPublisher.publishEvent(messageMetadata.toPollTaskFinished())

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            replyToMessageId = messageMetadata.replyTo.id
            text = """
                Poll stopped.
            """.trimIndent()
        }
    }

    fun batchUpdate(tasks: List<PollTask>) {
        pollTaskRepository.batchUpsert(tasks)
    }

    fun getTasks(): List<Task> {
        return pollTaskRepository.selectNotFinished()
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

    private fun MessageMetadata.toPollTaskFinished(): PollTaskFinished =
            PollTaskFinished(this.replyTo!!.id, this.createdAt)

}

data class PollTaskCreated(val task: PollTask)
data class PollTaskFinished(val messageId: Int, val finishedAt: Instant)

class NoAttachedMessagesFound() : BusinessException(
        """
    Message doesn't contain any attached messages. 
    Please reply to poll creation message to stop poll
    """
)

class NoPollsFound() : BusinessException("Poll was not found")
