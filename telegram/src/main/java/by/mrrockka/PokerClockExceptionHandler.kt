package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.interfaces.helper.ExceptionHandler
import eu.vendeli.tgbot.types.component.ProcessedUpdate
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger { }

object PokerClockExceptionHandler : ExceptionHandler {
    override suspend fun handle(exception: Throwable, update: ProcessedUpdate, bot: TelegramBot) {
        val chatid = update.origin.message?.chat?.id

        if (chatid == null) {
            logger.warn { "Update chat id is missing" }
            return
        }

        val message = when (exception) {
            is IllegalStateException -> exception.message
            else -> {
                logger.error(exception) { "" }
                "Exception occurred during handling"
            }
        } ?: "No exception message specified"
        sendMessage { message }.send(chatid, bot)
    }
}