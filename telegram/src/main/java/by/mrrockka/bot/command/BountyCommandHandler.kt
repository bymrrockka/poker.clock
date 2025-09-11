package by.mrrockka.bot.command

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.BountyTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface BountyCommandHandler {
    suspend fun bounty(message: MessageUpdate)
}

@Component
class BountyCommandHandlerImpl(
        private val bot: TelegramBot,
        private val bountyService: BountyTelegramService,
) : BountyCommandHandler {

    @CommandHandler(["/bounty"])
    override suspend fun bounty(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        bountyService.store(metadata)
                .also { bounty ->
                    sendMessage { "Bounty amount ${bounty.amount} from @${bounty.from.nickname} stored for @${bounty.to.nickname}" }
                            .send(to = metadata.chatId, via = bot)
                }
    }

}