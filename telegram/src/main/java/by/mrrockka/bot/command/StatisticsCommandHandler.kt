package by.mrrockka.bot.command

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Player
import by.mrrockka.domain.takenToGiven
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.domain.total
import by.mrrockka.service.statistics.PlayerStatisticsTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface StatisticsCommandHandler {
    suspend fun playerStats(message: MessageUpdate)
}

@Component
class StatisticsCommandHandlerImpl(
        private val bot: TelegramBot,
        private val playerStatisticsService: PlayerStatisticsTelegramService,
) : StatisticsCommandHandler {
    @CommandHandler(["/player_stats"])
    override suspend fun playerStats(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        playerStatisticsService.statistics(metadata)
                .also { player ->
                    sendMessage { player.toMessage() }
                            .send(to = metadata.chatId, via = bot)
                }
    }

    private fun Player.toMessage(): String =
            """
                |@${person.nickname} game statistics:
                |entries: ${entries.total()}
                |entries number: ${entries.size}
                |${
                when (this) {
                    is CashPlayer ->
                        """
                        |withdrawals: ${withdrawals.total()}
                        |game total: ${total()}
                    """.trimMargin()

                    is BountyPlayer -> takenToGiven()
                            .let { (taken, given) ->
                                """
                                |bounties:
                                |  taken: ${taken.size}
                                |  given: ${given.size}
                                |game total: ${total()}
                            """.trimMargin()
                            }

                    else -> ""
                }
            }""".trimMargin()

}