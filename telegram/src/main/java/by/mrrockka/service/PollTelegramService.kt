package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.PollAnswersRepo
import by.mrrockka.repo.PollTaskRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

interface PollTelegramService {
    fun create(metadata: MessageMetadata): PollTask
    fun stop(metadata: MessageMetadata)
    fun batchUpdate(tasks: List<PollTask>)
    fun selectActive(): List<Task>
    fun findParticipants(pollId: String): List<UUID>
}

@Service
@Transactional
open class PollTelegramServiceImpl(
        private val pollMessageParser: PollMessageParser,
        private val pollTaskRepository: PollTaskRepo,
        private val pollAnswersRepo: PollAnswersRepo,
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

    override fun findParticipants(pollId: String): List<UUID> {
        val participancyOptions = pollTaskRepository.selectByTgId(pollId)
                .also { poll ->
                    check(poll != null) { "Poll was not found" }
                    check(poll.options.find { it.participant } != null) { "Poll has no participant options" }
                }!!.options
                .mapIndexed { index, option -> option.participant to index }
                .groupBy({ it.first }, { it.second })

        val personAnswers = pollAnswersRepo.find(pollId)
        check(personAnswers.isNotEmpty()) { "No poll answers found" }

        val personId = participancyOptions[true]!!
                .flatMap { personAnswers[it] ?: emptyList() }

        check(personId.isNotEmpty()) { "Game participants not found according to poll" }
        return personId
    }

    private fun MessageMetadata.toPollTaskFinished(): PollEvent.Finished =
            PollEvent.Finished(this.replyTo!!.id, this.createdAt)
}

sealed class PollEvent {
    data class Created(val task: PollTask)
    data class Finished(val messageId: Long, val finishedAt: Instant)
}
