package by.mrrockka.service.statistics

import by.mrrockka.domain.TelegramGame
import by.mrrockka.domain.collection.PersonEntries
import by.mrrockka.domain.game.BountyGame
import by.mrrockka.domain.game.CashGame
import by.mrrockka.domain.game.TournamentGame
import by.mrrockka.domain.statistics.StatisticsCommand
import by.mrrockka.service.exception.ChatGameNotFoundException
import by.mrrockka.service.game.GameTelegramFacadeService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.math.BigDecimal

//todo: refactor
@Component
internal class GameStatisticsTelegramService(
        private val gameTelegramFacadeService: GameTelegramFacadeService,
) {
    fun retrieveStatistics(statisticsCommand: StatisticsCommand): BotApiMethodMessage {
        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(statisticsCommand.metadata)
                ?: throw ChatGameNotFoundException()

        return SendMessage().apply {
            chatId = statisticsCommand.metadata.chatId.toString()
            text = telegramGame.responseMessage()
            replyToMessageId = statisticsCommand.metadata.id
        }
    }
}

private fun TelegramGame.responseMessage(): String {
    return when (val game = this.game) {
        is CashGame -> """
            Cash game statistics:
                - players entered -> ${game.entries.size}
                - total buy-in amount -> ${game.entries.flatMap { it.entries }.sumOrZero()}
                - total withdrawal amount -> ${game.withdrawals?.map { it.total() }.sumOrZero()}
            """.trimIndent()

        is BountyGame -> """
            Bounty game statistics:
                - players entered -> ${game.entries.size}
                - number of entries -> ${game.entries.flatMap { it.entries }.size}
                - total buy-in amount -> ${game.entries entriesPlusBounties game.bountyAmount}
                - bounties out of game -> ${game.bountyList.size}
            """.trimIndent()

        is TournamentGame -> """
            Tournament game statistics:
                - players entered -> ${game.entries.size}
                - number of entries -> ${game.entries.flatMap { it.entries }.size}
                - total buy-in amount -> ${game.entries.map { it.total() }.sumOrZero()}
            """.trimIndent()

        else -> error("Unknown game")
    }
}

private fun Collection<BigDecimal>?.sumOrZero(): BigDecimal =
        if (this != null && this.isNotEmpty()) {
            this.reduce(BigDecimal::add)
        } else BigDecimal.ZERO

private infix fun List<PersonEntries>.entriesPlusBounties(amount: BigDecimal): BigDecimal {
    val entries = this.map { it.total() }
    return entries.sumOrZero() + entries.map { amount }.sumOrZero()
}