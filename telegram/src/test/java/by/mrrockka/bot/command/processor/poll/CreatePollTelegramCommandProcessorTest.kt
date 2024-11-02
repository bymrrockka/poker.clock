package by.mrrockka.bot.command.processor.poll

import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.service.TaskTelegramService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreatePollTelegramCommandProcessorTest {

    @MockK
    lateinit var taskTelegramService: TaskTelegramService

    @InjectMockKs
    lateinit var createPollTelegramCommandProcessor: CreatePollTelegramCommandProcessor

    @Test
    fun `service executed when processor receives the message`() {
        val message = MessageMetadataCreator.domain()
        val sendMessage = SendMessageCreator.api()

        every { taskTelegramService.createPoll(message) } returns sendMessage

        assertThat(createPollTelegramCommandProcessor.process(message)).isEqualTo(sendMessage)
    }


}