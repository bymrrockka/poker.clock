package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.HelpTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface HelpCommandHandler {
    suspend fun help(message: MessageUpdate)
}

@Component
class HelpCommandHandlerImpl(
        private val bot: TelegramBot,
        private val helpService: HelpTelegramService,
) : HelpCommandHandler {

    @CommandHandler(["/help"])
    override suspend fun help(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        helpService.help(metadata)
                .also { description ->
                    message { description.details ?: "No details available" }
                            .send(to = metadata.chatId, via = bot)
                }
    }

}