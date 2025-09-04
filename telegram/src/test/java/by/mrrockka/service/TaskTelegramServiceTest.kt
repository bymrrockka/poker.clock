package by.mrrockka.service

import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.creator.TaskCreator
import by.mrrockka.domain.PollTask
import by.mrrockka.parser.PollMessageParser
import by.mrrockka.repo.PollTaskRepo
import by.mrrockka.validation.poll.PollMessageValidator
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.support.CronExpression
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class TaskTelegramServiceTest {

    @MockK(relaxUnitFun = true)
    lateinit var pollMessageValidator: PollMessageValidator

    @MockK(relaxUnitFun = true)
    lateinit var pollTaskRepository: PollTaskRepo

    @MockK(relaxUnitFun = true)
    lateinit var pollMessageParser: PollMessageParser

    @MockK(relaxUnitFun = true)
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var taskTelegramService: TaskTelegramService

    @Test
    fun `when received message metadata to create poll return message with poll created text`() {
        val metadata = MessageMetadataCreator.domain()
        val cronExp = CronExpression.parse("* * 2 * * *")
        val poll = TaskCreator.poll.copy(
                cron = cronExp,
                message = metadata.text,
                options = listOf()
        )

        val argument = slot<PollTask>()

        every { pollTaskRepository.selectNotFinished() } returns listOf(poll)
        every { pollTaskRepository.upsert(capture(argument)) } just Runs
        every { pollMessageParser.parseCron(metadata) } returns cronExp
        every { pollMessageParser.parseMessageText(metadata) } returns metadata.text
        every { pollMessageParser.parseOptions(metadata) } returns listOf()

        val actual = taskTelegramService.createPoll(metadata)
        val expected = SendMessageCreator.api {
            it.chatId(metadata.chatId)
            it.replyToMessageId(metadata.id.toInt())
            it.text(
                    """
                Poll created.
                Will be triggered at ${cronExp.next(LocalDateTime.now())}
            """.trimIndent()
            )
        }

        assertThat(actual).isEqualTo(expected)
        verify { pollMessageValidator.validatePoll(any(PollTask::class)) }
        verify { pollTaskRepository.upsert(any(PollTask::class)) }

        assertThat(taskTelegramService.getTasks()).contains(poll)
        assertThat(argument.captured)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(poll)
    }

    @Test
    fun `when received message metadata to stop poll return message with poll stop text`() {
        val replyTo = MessageMetadataCreator.domainRandom()
        val metadata = MessageMetadataCreator.domain {
            it.replyTo(replyTo)
        }

        every { pollTaskRepository.finishPoll(replyTo.id, metadata.createdAt) } returns 1

        val actual = taskTelegramService.stopPoll(metadata)
        val expected = SendMessageCreator.api {
            it.chatId(metadata.chatId)
            it.replyToMessageId(metadata.replyTo?.id?.toInt())
            it.text(
                    """
                Poll stopped.
            """.trimIndent()
            )
        }

        assertThat(actual).isEqualTo(expected)
        verify { pollTaskRepository.finishPoll(replyTo.id, metadata.createdAt) }
        verify { eventPublisher.publishEvent(PollTaskFinished(replyTo.id, metadata.createdAt)) }
    }

    @Test
    fun `when received message metadata without reply message to stop poll throws exception`() {
        val metadata = MessageMetadataCreator.domain()
        assertThrows<IllegalStateException> { taskTelegramService.stopPoll(metadata) }
    }

    @Test
    fun `when received message metadata to stop poll and there is no related message id throws exception`() {
        val replyTo = MessageMetadataCreator.domainRandom()
        val metadata = MessageMetadataCreator.domain {
            it.replyTo(replyTo)
        }

        every { pollTaskRepository.finishPoll(replyTo.id, metadata.createdAt) } returns 0
        assertThrows<IllegalStateException> { taskTelegramService.stopPoll(metadata) }
    }

}