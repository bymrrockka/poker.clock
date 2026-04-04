package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.CommandType
import by.mrrockka.service.CancelTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.Guard
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface CancelCommandHandler {
    suspend fun cancel(message: MessageUpdate)
}

@Component
class CancelCommandHandlerImpl(
        private val bot: TelegramBot,
        private val cancelTelegramService: CancelTelegramService,
) : CancelCommandHandler {

    @CommandHandler(["/cancel"])
    @Guard(AdminGuard::class)
    override suspend fun cancel(message: MessageUpdate) {
        val metadata = message.toMessageMetadata()
        cancelTelegramService.cancel(metadata)
                .also { commandType ->
                    message {
                        when (commandType) {
                            CommandType.ENTRY -> "Entry canceled"
                            CommandType.BOUNTY -> "Bounty canceled"
                            CommandType.WITHDRAWAL -> "Withdrawal canceled"
                        }
                    }.send(to = metadata.chatId, via = bot)
                }
    }

}