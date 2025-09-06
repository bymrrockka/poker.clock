package by.mrrockka.bot.command

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.EntryTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface EntryCommandHandler {
    suspend fun entry(message: MessageUpdate)
}

@Component
class EntryCommandHandlerImpl(
        private val bot: TelegramBot,
        private val entryTelegramService: EntryTelegramService,
) : EntryCommandHandler {

    @CommandHandler(["/entry", "/reentry"])
    override suspend fun entry(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        entryTelegramService.entry(metadata)
                .also { (nicknames, amount) ->
                    sendMessage {
                        """
                        |Stored entries: 
                        |${nicknames.joinToString { "|  - @${it} -> ${amount.setScale(0)}" }}
                        """.trimMargin()
                    }.send(to = metadata.chatId, via = bot)
                }
    }
}