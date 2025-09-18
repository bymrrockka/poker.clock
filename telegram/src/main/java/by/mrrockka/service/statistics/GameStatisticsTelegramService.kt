package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.moneyInGame
import by.mrrockka.domain.total
import by.mrrockka.domain.totalEntries
import by.mrrockka.service.GameTelegramService
import org.springframework.stereotype.Component

@Component
class GameStatisticsTelegramService(
        private val gameService: GameTelegramService,
) : StatisticsService {
    override fun statistics(metadata: MessageMetadata): String {
        return gameService.findGame(metadata).toMessage()
    }
}

private fun Game.toMessage(): String {
    return when (this) {
        is CashGame -> """
            |Cash game statistics:
            |  - players entered -> ${players.size}
            |  - total buy-in amount -> ${players.totalEntries()}
            |  - total withdrawal amount -> ${players.flatMap { it.withdrawals }.total()}
            |  - total in game -> ${moneyInGame()}
            """.trimMargin()

        is BountyTournamentGame -> """
            |Bounty game statistics:
            |  - players entered -> ${players.size}
            |  - number of entries -> ${players.flatMap { it.entries }.size}
            |  - bounties out of game -> ${players.flatMap { it.bounties }.size / 2}
            |  - total in game -> ${moneyInGame()}
            """.trimMargin()

        is TournamentGame -> """
            |Tournament game statistics:
            |  - players entered -> ${players.size}
            |  - number of entries -> ${players.flatMap { it.entries }.size}
            |  - total in game -> ${moneyInGame()}
            """.trimMargin()

        else -> error("Unknown game type")
    }
}
