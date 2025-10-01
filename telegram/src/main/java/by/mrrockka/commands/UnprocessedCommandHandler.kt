package by.mrrockka.commands

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

@Component
class UnprocessedCommandHandler(
        private val bot: TelegramBot,
) {

    @UnprocessedHandler
    suspend fun unprocessed(message: MessageUpdate?) {
        if (message != null)
            sendMessage { "Can't process command" }.send(message.message.chat.id, bot)
    }
}