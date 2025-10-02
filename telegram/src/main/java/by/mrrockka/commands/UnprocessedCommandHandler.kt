package by.mrrockka.commands

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.msg.EntityType
import org.springframework.stereotype.Component

@Component
class UnprocessedCommandHandler(
        private val bot: TelegramBot,
) {

    @UnprocessedHandler
    suspend fun unprocessed(message: MessageUpdate?) {
        message?.message?.entities
                ?.find { it.type == EntityType.BotCommand }
                ?.also { command ->
                    sendMessage { "Can't process command $command" }
                            .send(message.message.chat.id, bot)
                }
    }
}