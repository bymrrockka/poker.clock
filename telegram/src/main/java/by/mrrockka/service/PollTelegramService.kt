package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.PollTaskRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.Instant
import kotlin.time.ExperimentalTime

interface PollTelegramService {
    fun create(messageMetadata: MessageMetadata): PollTask
    fun stop(messageMetadata: MessageMetadata): BotApiMethodMessage
    fun batchUpdate(tasks: List<PollTask>)
    fun selectActive(): List<Task>
}

@Service
@OptIn(ExperimentalTime::class)
class PollTelegramServiceImpl(
        private val pollMessageParser: PollMessageParser,
        private val pollTaskRepository: PollTaskRepo,
        private val eventPublisher: ApplicationEventPublisher,
) : PollTelegramService {

    override fun create(messageMetadata: MessageMetadata): PollTask {
        val pollTask = pollMessageParser.parse(messageMetadata)
        check(pollTask.options.isNotEmpty()) { "Poll options should be specified" }
        pollTaskRepository.upsert(pollTask)
        eventPublisher.publishEvent(PollEvent.Created(pollTask))

        return pollTask
    }

    override fun stop(messageMetadata: MessageMetadata): BotApiMethodMessage {
        check(messageMetadata.replyTo != null) {
            """
                Message doesn't contain any attached messages. 
                Please reply to poll creation message to stop poll
                """
        }

        val size = pollTaskRepository.finishPoll(
                messageMetadata.replyTo.id,
                messageMetadata.createdAt,
        )
        check(size != 0) { "Poll was not found" }
        eventPublisher.publishEvent(messageMetadata.toPollTaskFinished())

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            replyToMessageId = messageMetadata.replyTo.id.toInt()
            text = "Poll stopped."
        }
    }

    override fun batchUpdate(tasks: List<PollTask>) {
        pollTaskRepository.batchUpsert(tasks)
    }

    override fun selectActive(): List<Task> {
        return pollTaskRepository.selectActive()
    }

    private fun MessageMetadata.toPollTaskFinished(): PollEvent.Finished =
            PollEvent.Finished(this.replyTo!!.id, this.createdAt)
}

sealed class PollEvent {
    data class Created(val task: PollTask)
    data class Finished(val messageId: Long, val finishedAt: Instant)
}
