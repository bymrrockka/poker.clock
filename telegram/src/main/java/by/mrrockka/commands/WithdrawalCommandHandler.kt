package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.WithdrawalTelegramService
import by.mrrockka.service.up
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.Guard
import eu.vendeli.tgbot.api.message.message
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

    @CommandHandler(["/withdrawal"])
    @Guard(ExcludeBotGuard::class)
    override suspend fun withdraw(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        withdrawalService.withdraw(metadata)
                .also { (nickname, amount) ->
                    message {
                        """
                        |Stored withdrawals: 
                        |  - @${nickname} -> ${amount.up()}
                        """.trimMargin()
                    }.send(to = metadata.chatId, via = bot)
                }
    }
}
