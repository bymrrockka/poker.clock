package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.FinalePlacesMessageParser
import by.mrrockka.repo.FinalePlacesRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface FinalePlacesTelegramService {
    fun store(message: MessageMetadata): List<FinalPlace>
    fun store(message: MessageMetadata, finalePlaces: Map<Int, String>): List<FinalPlace>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class FinalePlacesTelegramServiceImpl(
        private val finalePlacesRepo: FinalePlacesRepo,
        private val finalePlacesParser: FinalePlacesMessageParser,
        private val gameService: GameTelegramService,
        private val personService: TelegramPersonService,
) : FinalePlacesTelegramService {

    override fun store(message: MessageMetadata): List<FinalPlace> {
        message.checkMentions()
        val places = finalePlacesParser.parse(message)
        return store(message, places)
    }

    override fun store(message: MessageMetadata, finalePlaces: Map<Int, String>): List<FinalPlace> {
        check(finalePlaces.isNotEmpty()) { "Finale places could not be empty" }

        val game = gameService.findGame(message)
        check(game !is CashGame) { "Finale places is not allowed for cash game" }
        val persons = personService.findByNicknames(finalePlaces.values.toList(), message.chatId)
                .associateBy { it.nickname }
        val mapped = finalePlaces
                .map { (position, nickname) ->
                    FinalPlace(position, persons[nickname] ?: error("Person not found for $nickname"))
                }
        finalePlacesRepo.store(game.id, mapped)

        return mapped
    }
}
