package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.interfaces.helper.ExceptionHandler
import eu.vendeli.tgbot.types.component.ProcessedUpdate
import org.slf4j.helpers.Reporter.warn

object PokerClockExceptionHandler : ExceptionHandler {
    override suspend fun handle(exception: Throwable, update: ProcessedUpdate, bot: TelegramBot) {
        val chatid = update.origin.message?.chat?.id

        if (chatid == null) {
            warn("Update chat id is missing")
            return
        }

        val message = when (exception) {
            is IllegalStateException -> exception.message
            else -> "Exception occurred during handling"
        } ?: "Exception occurred during handling"
        sendMessage { message }.send(chatid, bot)
    }
}