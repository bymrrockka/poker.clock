package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.FinalePlacesMessageParser
import by.mrrockka.repo.FinalePlacesRepo
import org.springframework.stereotype.Service

interface FinalePlacesTelegramService {
    fun store(message: MessageMetadata): List<FinalPlace>
}

@Service
class FinalePlacesTelegramServiceImpl(
        private val finalePlacesRepo: FinalePlacesRepo,
        private val finalePlacesParser: FinalePlacesMessageParser,
        private val gameService: GameTelegramService,
        private val personService: TelegramPersonService,
) : FinalePlacesTelegramService {

    override fun store(message: MessageMetadata): List<FinalPlace> {
        message.checkMentions()

        val places = finalePlacesParser.parse(message)
        check(places.isNotEmpty()) { "Finale places could not be empty" }

        val telegramGame = gameService.findGame(message)
        check(telegramGame.game !is CashGame) { "Finale places is not allowed for cash game" }

        val persons = personService.findByMessage(message)
                .associateBy { it.nickname }

        val finalePlaces = places
                .map { (position, nickname) ->
                    FinalPlace(position, persons[nickname] ?: error("Person not found for $nickname"))
                }
        finalePlacesRepo.store(telegramGame.game.id, finalePlaces)

        return finalePlaces
    }
}
