package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.ChatGame
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.moneyInGame
import by.mrrockka.domain.statistics.StatisticsCommand
import by.mrrockka.domain.total
import by.mrrockka.domain.totalEntries
import by.mrrockka.service.GameTelegramService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Component
class GameStatisticsTelegramService(
        private val gameTelegramFacadeService: GameTelegramService,
) {
    fun retrieveStatistics(statisticsCommand: StatisticsCommand): BotApiMethodMessage {
        val telegramGame = gameTelegramFacadeService
                .findGame(statisticsCommand.metadata)

        return SendMessage().apply {
            chatId = statisticsCommand.metadata.chatId.toString()
            text = telegramGame.responseMessage()
            replyToMessageId = statisticsCommand.metadata.id
        }
    }
}

private fun ChatGame.responseMessage(): String {
    return when (val game = this.game) {
        is CashGame -> """
            |Cash game statistics:
            |  - players entered -> ${game.players.size}
            |  - total buy-in amount -> ${game.players.totalEntries()}
            |  - total withdrawal amount -> ${game.players.flatMap { it.withdrawals }.total()}
            |  - total in game -> ${game.moneyInGame()}
            """.trimMargin()

        is BountyTournamentGame -> """
            |Bounty game statistics:
            |  - players entered -> ${game.players.size}
            |  - number of entries -> ${game.players.flatMap { it.entries }.size}
            |  - total buy-in amount -> ${game.moneyInGame()}
            |  - bounties out of game -> ${game.players.flatMap { it.bounties }.size / 2}
            """.trimMargin()

        is TournamentGame -> """
            |Tournament game statistics:
            |  - players entered -> ${game.players.size}
            |  - number of entries -> ${game.players.flatMap { it.entries }.size}
            |  - total buy-in amount -> ${game.moneyInGame()}
            """.trimMargin()

        else -> error("Unknown game")
    }
}