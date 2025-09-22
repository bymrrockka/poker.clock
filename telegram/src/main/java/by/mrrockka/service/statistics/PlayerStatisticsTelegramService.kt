package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import by.mrrockka.domain.takenToGiven
import by.mrrockka.domain.total
import by.mrrockka.service.GameTelegramService
import org.springframework.stereotype.Component

@Component
class PlayerStatisticsTelegramService(
        private val gameService: GameTelegramService,
) : StatisticsService {

    override fun statistics(metadata: MessageMetadata): String {
        check(metadata.from?.username != null) { "User might have username" }
        val game = gameService.findGame(metadata)
        return game.players.find { it.person.nickname == metadata.from.username }?.toMessage()
                ?: error("User ${metadata.from.username} hasn't enter the game")
    }
}

private fun Player.toMessage(): String =
        when (this) {
            is CashPlayer -> """
                |@${person.nickname} game statistics:
                |entries: ${entries.total()}
                |entries number: ${entries.size}
                |withdrawals: ${withdrawals.total()}
                |game total: ${total()}
            """.trimMargin()

            is BountyPlayer -> takenToGiven().let { (taken, given) ->
                """
                    |@${person.nickname} game statistics:
                    |entries: ${entries.total()}
                    |entries number: ${entries.size}
                    |bounties:
                    |  taken: ${taken.size}
                    |  given: ${given.size}
                    |game total: ${total()}
                """.trimMargin()
            }

            is TournamentPlayer -> """
                |@${person.nickname} game statistics:
                |entries: ${entries.total()}
                |entries number: ${entries.size}
            """.trimMargin()

            else -> error("Player type is not recognized")
        }
