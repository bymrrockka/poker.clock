package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.statistics.GameStatisticsTelegramService
import by.mrrockka.service.statistics.MyChatStatisticsTelegramService
import by.mrrockka.service.statistics.PlayerStatisticsTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface StatisticsCommandHandler {
    suspend fun playerStats(message: MessageUpdate)
    suspend fun gameStats(message: MessageUpdate)
    suspend fun myStats(message: MessageUpdate)
}

@Component
class StatisticsCommandHandlerImpl(
        private val bot: TelegramBot,
        private val playerStatisticsService: PlayerStatisticsTelegramService,
        private val gameStatisticsService: GameStatisticsTelegramService,
        private val myChatStatisticsService: MyChatStatisticsTelegramService,
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

    @CommandHandler(["/my_stats"])
    override suspend fun myStats(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        myChatStatisticsService.statistics(metadata)
                .also { message ->
                    sendMessage { message }
                            .send(to = metadata.chatId, via = bot)
                }
    }

}
