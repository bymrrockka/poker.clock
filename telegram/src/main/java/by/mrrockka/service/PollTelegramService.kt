package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.PollTaskRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.Instant

interface PollTelegramService {
    fun create(metadata: MessageMetadata): PollTask
    fun stop(metadata: MessageMetadata)
    fun batchUpdate(tasks: List<PollTask>)
    fun selectActive(): List<Task>
}

@Service
class PollTelegramServiceImpl(
        private val pollMessageParser: PollMessageParser,
        private val pollTaskRepository: PollTaskRepo,
        private val eventPublisher: ApplicationEventPublisher,
) : PollTelegramService {

    override fun create(metadata: MessageMetadata): PollTask {
        val pollTask = pollMessageParser.parse(metadata)
        check(pollTask.options.isNotEmpty()) { "No options found" }
        pollTaskRepository.store(pollTask)
        eventPublisher.publishEvent(PollEvent.Created(pollTask))

        return pollTask
    }

    override fun stop(metadata: MessageMetadata) {
        val size = pollTaskRepository.finish(
                metadata.replyTo!!.id,
                metadata.createdAt,
        )
        check(size != 0) { "Poll was not found" }
        eventPublisher.publishEvent(metadata.toPollTaskFinished())
    }

    override fun batchUpdate(tasks: List<PollTask>) {
        pollTaskRepository.store(tasks)
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
