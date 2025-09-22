package by.mrrockka.service.statistics

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.TransferType
import by.mrrockka.domain.total
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.MoneyTransferRepo
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Component

@Component
class MyChatStatisticsTelegramService(
        private val personService: TelegramPersonService,
        private val gameTelegramService: GameTelegramService,
        private val entriesRepo: EntriesRepo,
        private val moneyTransferRepo: MoneyTransferRepo,
) : StatisticsService {

    override fun statistics(metadata: MessageMetadata): String {
        val person = personService.findByFrom(metadata)
        val entries = entriesRepo.findByPerson(person)
        val games = gameTelegramService.findByChat(metadata)
                .filter { it.id in entries.keys }
        val (firstPlaces, otherPlaces) = games
                .filter { it is TournamentGame || it is BountyTournamentGame }
                .flatMap { game ->
                    when (game) {
                        is TournamentGame -> game.finalePlaces ?: emptyList()
                        is BountyTournamentGame -> game.finalePlaces ?: emptyList()
                        else -> emptyList()
                    }
                }.filter { finalPlaces -> finalPlaces.person == person }
                .partition { it.position == 1 }

        val moneyTransfers = moneyTransferRepo.findByPerson(person)
                .filter { it.type == TransferType.CREDIT }

        return """
           |nickname: @${metadata.from!!.username!!}
           |games played: ${games.size}
           |buy-ins total: ${entries.values.flatten().total()}
           |won total: ${moneyTransfers.map { it.amount }.total()} 
           |times in prizes: ${firstPlaces.size + otherPlaces.size}
           |times in first place: ${firstPlaces.size}
        """.trimMargin()
    }
}
