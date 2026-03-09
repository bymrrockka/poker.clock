package by.mrrockka.service.statistics

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.total
import by.mrrockka.repo.PlayerSummaryRepo
import by.mrrockka.service.BountyTournamentPlayerSummary
import by.mrrockka.service.CashPlayerSummary
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PlayerPrizeSummary
import by.mrrockka.service.PlayerSummary
import by.mrrockka.service.TelegramPersonService
import by.mrrockka.service.TournamentPlayerSummary
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MyChatStatisticsTelegramService(
        private val personService: TelegramPersonService,
        private val gameTelegramService: GameTelegramService,
        private val gameSummaryRepo: PlayerSummaryRepo,
) : StatisticsService {

    override fun statistics(metadata: MessageMetadata): String {
        val chatGames = gameTelegramService.gameIdsByChat(metadata)
        val person = personService.findByFrom(metadata)
        val gameSummaries = gameSummaryRepo.findForPersonGames(chatGames, person.id)
        val entriesTotal = gameSummaries
                .map { summary ->
                    when (summary) {
                        is BountyTournamentPlayerSummary -> summary.entries() + (summary.bounty.total * BigDecimal(summary.entriesNum))
                        else -> summary.entries()
                    }
                }.total()
        val tournaments = gameSummaries
                .filter { it is PlayerPrizeSummary }
                .map { it as PlayerPrizeSummary }
        val (firstPlaces, otherPlaces) = tournaments
                .filter { it.position != null }
                .partition { it.position == 1 }

        val won = gameSummaries.map { it.won() }.total()

        return """
           |nickname: @${person.nickname!!}
           |games played: ${gameSummaries.size} (${tournaments.size} tournament${if (tournaments.size == 1) "" else "s"})
           |times in prizes: ${firstPlaces.size + otherPlaces.size}
           |times in first place: ${firstPlaces.size}
           |buy-ins total: ${entriesTotal}
           |won total: ${won} 
           |correlation: ${won - entriesTotal} 
        """.trimMargin()
    }

    private fun PlayerSummary.won(): BigDecimal {
        return when (this) {
            is TournamentPlayerSummary -> prize
            is BountyTournamentPlayerSummary -> prize + bounty.total
            is CashPlayerSummary -> withdrawals
            else -> error("Unknown game summary type")
        }
    }
}
