package by.mrrockka.bot.command

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.statistics.GameStatisticsTelegramService
import by.mrrockka.service.statistics.PlayerStatisticsTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface StatisticsCommandHandler {
    suspend fun playerStats(message: MessageUpdate)
    suspend fun gameStats(message: MessageUpdate)
}

@Component
class StatisticsCommandHandlerImpl(
        private val bot: TelegramBot,
        private val playerStatisticsService: PlayerStatisticsTelegramService,
        private val gameStatisticsService: GameStatisticsTelegramService,
) : StatisticsCommandHandler {
    @CommandHandler(["/player_stats"])
    override suspend fun playerStats(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        playerStatisticsService.statistics(metadata)
                .also { message ->
                    sendMessage { message }
                            .send(to = metadata.chatId, via = bot)
                }
    }

    @CommandHandler(["/game_stats"])
    override suspend fun gameStats(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        gameStatisticsService.statistics(metadata)
                .also { message ->
                    sendMessage { message }
                            .send(to = metadata.chatId, via = bot)
                }
    }

}
