package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.ChatGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.statistics.StatisticsCommand
import by.mrrockka.domain.total
import by.mrrockka.service.GameTelegramService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.math.BigDecimal

//todo: refactor
@Component
internal class GameStatisticsTelegramService(
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
            Cash game statistics:
                - players entered -> ${game.players.size}
                - total buy-in amount -> ${game.moneyInGame()}
                - total withdrawal amount -> ${game.players.flatMap { it.withdrawals }.total()}
            """.trimIndent()

        is BountyTournamentGame -> """
            Bounty game statistics:
                - players entered -> ${game.players.size}
                - number of entries -> ${game.players.flatMap { it.entries }.size}
                - total buy-in amount -> ${game.moneyInGame()}
                - bounties out of game -> ${game.players.flatMap { it.bounties }.size / 2}
            """.trimIndent()

        is TournamentGame -> """
            Tournament game statistics:
                - players entered -> ${game.players.size}
                - number of entries -> ${game.players.flatMap { it.entries }.size}
                - total buy-in amount -> ${game.moneyInGame()}
            """.trimIndent()

        else -> error("Unknown game")
    }
}

private fun Game.moneyInGame(): BigDecimal =
        when (val game = this) {
            is CashGame -> game.players.flatMap { it.entries }.total()
            is BountyTournamentGame -> game.players.flatMap { it.entries }.total() + (game.players.sumOf { it.entries.size }.toBigDecimal() * game.bounty)
            is TournamentGame -> game.players.flatMap { it.entries }.total()
            else -> error("Unknown game")
        }