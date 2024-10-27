package by.mrrockka.bot.command.processor.poll

import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.service.TaskTelegramService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CreatePollTelegramCommandProcessorTest {

    @Mock
    lateinit var taskTelegramService: TaskTelegramService

    @InjectMocks
    lateinit var createPollTelegramCommandProcessor: CreatePollTelegramCommandProcessor


    @Test
    fun `service executed when processor receives the message`() {
        val message = MessageMetadataCreator.domain()
        val sendMessage = SendMessageCreator.api()

        Mockito.`when`(taskTelegramService.createPoll(message))
                .thenReturn(sendMessage)

        assertThat(createPollTelegramCommandProcessor.process(message)).isEqualTo(sendMessage)
    }


}