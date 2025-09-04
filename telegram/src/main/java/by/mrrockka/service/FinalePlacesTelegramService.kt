package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.finaleplaces.FinalPlace
import by.mrrockka.domain.finaleplaces.FinalePlaces
import by.mrrockka.parser.FinalePlacesMessageParser
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
        val gameTelegramFacadeService: GameTelegramService,
        val telegramPersonServiceOld: TelegramPersonServiceOld,
        val personMentionsValidator: PersonMentionsValidator,
) {

    fun storePrizePool(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        personMentionsValidator.validateMessageMentions(messageMetadata, 1)

        val places = finalePlacesMessageParser.parse(messageMetadata)
        check(places.isNotEmpty()) { "Finale places could not be empty" }

        val telegramGame = gameTelegramFacadeService .findGame(messageMetadata)
        check(telegramGame.game !is CashGame) { "Finale places is not allowed for cash game" }

        val telegramPersons = telegramPersonServiceOld
                .getAllByNicknamesAndChatId(places.values.toList(), messageMetadata.chatId)
                .associateBy { it.nickname }

        val finalePlaces = FinalePlaces(places
                .map { (position, nickname) -> FinalPlace(position, telegramPersons[nickname]) }
                .toList())

        finalePlacesService.store(telegramGame.game.id, finalePlaces)
        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("""
                    Finale places stored:
                    ${finalePlaces.finalPlaces.joinToString { "${it.position}. -> @${it.person.nickname}" }}
                """.trimIndent())
                .replyToMessageId(telegramGame.messageMetadata.id.toInt())
                .build()

    }
}
