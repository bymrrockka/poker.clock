package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.interfaces.helper.ExceptionHandler
import eu.vendeli.tgbot.types.component.ProcessedUpdate

object PokerClockExceptionHandler : ExceptionHandler {
    override suspend fun handle(exception: Throwable, update: ProcessedUpdate, bot: TelegramBot) {
        val chatid = update.origin.message?.chat?.id ?: error("Update chat id is missing")

        sendMessage { exception.message ?: "Exception occurred during handling" }
                .send(chatid, bot)
    }
}