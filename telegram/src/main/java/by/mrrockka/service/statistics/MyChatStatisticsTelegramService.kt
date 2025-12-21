package by.mrrockka.service.statistics

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentSummary
import by.mrrockka.domain.total
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.GameSummaryRepo
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Component

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
                .filter { it is TournamentSummary }
                .filter { it.person == person }
                .map { it as TournamentSummary }
                .partition { it.position == 1 }

        return """
           |nickname: @${person.nickname!!}
           |games played: ${gameSummaries.size}
           |buy-ins total: ${entriesTotal}
           |won total: ${gameSummaries.map { it.amount }.total()} 
           |times in prizes: ${firstPlaces.size + otherPlaces.size}
           |times in first place: ${firstPlaces.size}
        """.trimMargin()
    }
}
