package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.interfaces.helper.ExceptionHandler
import eu.vendeli.tgbot.types.component.ProcessedUpdate

object PokerClockExceptionHandler : ExceptionHandler {
    override suspend fun handle(exception: Throwable, update: ProcessedUpdate, bot: TelegramBot) {
        val chatid = update.origin.message?.chat?.id ?: error("Update chat id is missing")

        val message = when (exception) {
            is IllegalStateException -> exception.message
            else -> "Exception occurred during handling"
        } ?: "Exception occurred during handling"
        sendMessage { message }.send(chatid, bot)
    }
}