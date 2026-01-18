package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.PinMessageService
import by.mrrockka.service.PrizePoolTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.stereotype.Component

interface PrizePoolCommandHandler {
    suspend fun prizePool(message: MessageUpdate)
}

@Component
class PrizePoolCommandHandlerImpl(
        private val bot: TelegramBot,
        private val prizePoolService: PrizePoolTelegramService,
        private val pinMessageService: PinMessageService,
) : PrizePoolCommandHandler {

    @CommandHandler(["/prize_pool", "/pp"])
    override suspend fun prizePool(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        prizePoolService.store(metadata)
                .let { prizePool ->
                    message {
                        """
                        |Prize pool stored:
                        |${prizePool.joinToString("\n") { "${it.position}. ${it.percentage}%" }}
                        """.trimMargin()
                    }.sendReturning(to = metadata.chatId, via = bot)
                            .onFailure { error("Failed to send prize pool message") }
                            ?: error("No message returned from telegram api")
                }.also { message -> pinMessageService.pin(message, PinType.GAME) }
    }

}