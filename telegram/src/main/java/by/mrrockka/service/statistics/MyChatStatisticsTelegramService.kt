package by.mrrockka.service.statistics

import by.mrrockka.domain.BountySummary
import by.mrrockka.domain.CashSummary
import by.mrrockka.domain.GameSummary
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PrizeGameSummary
import by.mrrockka.domain.TournamentSummary
import by.mrrockka.domain.total
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.GameSummaryRepo
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MyChatStatisticsTelegramService(
        private val personService: TelegramPersonService,
        private val gameTelegramService: GameTelegramService,
        private val entriesRepo: EntriesRepo,
        private val gameSummaryRepo: GameSummaryRepo,
) : StatisticsService {

    override fun statistics(metadata: MessageMetadata): String {
        val chatGames = gameTelegramService.gameIdsByChat(metadata)
        val person = personService.findByFrom(metadata)
        val entriesTotal = entriesRepo.totalForPersonGames(chatGames, person.id)
        val gameSummaries = gameSummaryRepo.findForPersonGames(chatGames, person.id)
        val (firstPlaces, otherPlaces) = gameSummaries
                .filter { it.person == person }
                .filter { it is PrizeGameSummary }
                .map { it as PrizeGameSummary }
                .partition { it.position == 1 }

        return """
           |nickname: @${person.nickname!!}
           |games played: ${gameSummaries.size}
           |buy-ins total: ${entriesTotal}
           |won total: ${gameSummaries.map { it.won() }.total()} 
           |times in prizes: ${firstPlaces.size + otherPlaces.size}
           |times in first place: ${firstPlaces.size}
        """.trimMargin()
    }

    private fun GameSummary.won(): BigDecimal {
        return when (this) {
            is TournamentSummary -> prize
            is BountySummary -> prize + takenBounties
            is CashSummary -> withdrawals
            else -> error("Unknown game summary type")
        }
    }
}
