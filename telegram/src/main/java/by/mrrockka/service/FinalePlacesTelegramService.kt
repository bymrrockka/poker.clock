package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.finaleplaces.FinalPlace
import by.mrrockka.domain.finaleplaces.FinalePlaces
import by.mrrockka.parser.finaleplaces.FinalePlacesMessageParser
import by.mrrockka.service.exception.ChatGameNotFoundException
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.GameValidator
import by.mrrockka.validation.collection.CollectionsValidator
import by.mrrockka.validation.mentions.PersonMentionsValidator
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
@RequiredArgsConstructor
class FinalePlacesTelegramService(
        val finalePlacesService: FinalePlacesService,
        val finalePlacesMessageParser: FinalePlacesMessageParser,
        val gameTelegramFacadeService: GameTelegramFacadeService,
        val telegramPersonService: TelegramPersonService,
        val gameValidator: GameValidator,
        val personMentionsValidator: PersonMentionsValidator,
        val collectionsValidator: CollectionsValidator,
) {

    fun storePrizePool(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        personMentionsValidator.validateMessageMentions(messageMetadata, 1)

        val places = finalePlacesMessageParser.parse(messageMetadata)
        collectionsValidator.validateMapIsNotEmpty(places, "Finale places")

        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(messageMetadata)
                .orElseThrow { ChatGameNotFoundException() }
        gameValidator.validateGameIsTournamentType(telegramGame.game)

        val telegramPersons = telegramPersonService
                .getAllByNicknamesAndChatId(places.values.toList(), messageMetadata.chatId)
                .associateBy { it.nickname }

        val finalePlaces = FinalePlaces(places
                .map { (position, nickname) -> FinalPlace(position, telegramPersons[nickname]) }
                .toList())

        finalePlacesService.store(telegramGame.game.getId(), finalePlaces)
        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("""
                    Finale places stored:
                    ${finalePlaces.finalPlaces.joinToString { "${it.position}. -> @${it.person.nickname}" }}
                """.trimIndent())
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()

    }
}
