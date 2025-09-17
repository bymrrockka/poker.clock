package by.mrrockka.service.statistics

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Player
import by.mrrockka.service.GameTelegramService
import org.springframework.stereotype.Component

interface PlayerStatisticsTelegramService {
    fun statistics(metadata: MessageMetadata): Player
}

@Component
class PlayerStatisticsTelegramServiceImpl(
        private val gameService: GameTelegramService,
) : PlayerStatisticsTelegramService {

    override fun statistics(metadata: MessageMetadata): Player {
        check(metadata.from?.username != null) { "User might have username" }
        val telegramGame = gameService.findGame(metadata)
        return telegramGame.game.players.find { it.person.nickname == metadata.from.username }
                ?: error("User ${metadata.from.username} hasn't enter the game")
    }
}
