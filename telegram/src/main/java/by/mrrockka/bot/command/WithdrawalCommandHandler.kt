package by.mrrockka.bot.command

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.WithdrawalTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface WithdrawalCommandHandler {
    suspend fun withdraw(message: MessageUpdate)
}

@Component
class WithdrawalCommandHandlerImpl(
        private val bot: TelegramBot,
        private val withdrawalService: WithdrawalTelegramService,
) : WithdrawalCommandHandler {

    @CommandHandler(["/withdraw", "/withdrawal"])
    override suspend fun withdraw(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        withdrawalService.withdraw(metadata)
                .also { (amount, nicknames) ->
                    sendMessage {
                        """
                        |Stored withdrawals: 
                        |${nicknames.joinToString { "|  - @${it} -> $amount" }}
                        """.trimMargin()
                    }.send(to = metadata.chatId, via = bot)
                }
    }
}
