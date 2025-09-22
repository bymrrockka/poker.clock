package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.PrizePoolTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface PrizePoolCommandHandler {
    suspend fun prizePool(message: MessageUpdate)
}

@Component
class PrizePoolCommandHandlerImpl(
        private val bot: TelegramBot,
        private val prizePoolService: PrizePoolTelegramService,
) : PrizePoolCommandHandler {

    @CommandHandler(["/prize_pool", "/pp"])
    override suspend fun prizePool(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        prizePoolService.store(metadata)
                .also { prizePool ->
                    sendMessage {
                        """
                        |Prize pool stored:
                        |${prizePool.joinToString("\n") { "${it.position}. ${it.percentage}%" }}
                        """.trimMargin()
                    }.send(to = metadata.chatId, via = bot)
                }
    }

}