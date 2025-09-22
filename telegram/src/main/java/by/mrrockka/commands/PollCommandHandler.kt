package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.PollTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

interface PollCommandHandler {
    suspend fun create(message: MessageUpdate)
    suspend fun stop(message: MessageUpdate)
}

@Component
class PollCommandHandlerImpl(
        private val bot: TelegramBot,
        private val pollService: PollTelegramService,
) : PollCommandHandler {

    @CommandHandler(["/create_poll", "/cp"])
    override suspend fun create(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        pollService.create(metadata)
                .also { poll ->
                    sendMessage {
                        """
                            |Poll created.
                            |Will be triggered next ${poll.cron.next(LocalDateTime.now())?.dayOfWeek?.name}
                        """.trimMargin()
                    }.send(to = metadata.chatId, bot)
                }
    }

    @CommandHandler(["/stop_poll", "/sp"])
    override suspend fun stop(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        check(metadata.replyTo != null) {
            """
                |Message doesn't contain any attached messages. 
                |Please reply to poll creation message to stop poll
                """.trimMargin()
        }

        pollService.stop(metadata)
                .also { sendMessage { "Poll stopped" }.send(to = metadata.chatId, bot) }
    }

}

