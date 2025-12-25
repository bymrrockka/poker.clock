package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyTournamentSummary
import by.mrrockka.domain.CashSummary
import by.mrrockka.domain.GameSummary
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PrizeGameSummary
import by.mrrockka.domain.TournamentSummary
import by.mrrockka.domain.total
import by.mrrockka.repo.GameSummaryRepo
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MyChatStatisticsTelegramService(
        private val personService: TelegramPersonService,
        private val gameTelegramService: GameTelegramService,
        private val gameSummaryRepo: GameSummaryRepo,
) : StatisticsService {

    override fun statistics(metadata: MessageMetadata): String {
        val chatGames = gameTelegramService.gameIdsByChat(metadata)
        val person = personService.findByFrom(metadata)
        val gameSummaries = gameSummaryRepo.findForPersonGames(chatGames, person.id)
        val entriesTotal = gameSummaries
                .map { summary ->
                    when (summary) {
                        is BountyTournamentSummary -> summary.entries() + (summary.bounty.amount * BigDecimal(summary.entriesNum))
                        else -> summary.entries()
                    }
                }.total()
        val tournaments = gameSummaries
                .filter { it is PrizeGameSummary }
                .map { it as PrizeGameSummary }
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

    private fun GameSummary.won(): BigDecimal {
        return when (this) {
            is TournamentSummary -> prize
            is BountyTournamentSummary -> prize + bounty.taken
            is CashSummary -> withdrawals
            else -> error("Unknown game summary type")
        }
    }
}
