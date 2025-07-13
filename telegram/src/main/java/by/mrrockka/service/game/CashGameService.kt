package by.mrrockka.service.game

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.mapper.TelegramGameMapper
import by.mrrockka.parser.game.GameMessageParser
import by.mrrockka.repo.game.TelegramGameRepository
import by.mrrockka.service.EntriesService
import by.mrrockka.service.GameServiceOld
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
internal open class CashGameService(
    private val telegramGameRepository: TelegramGameRepository,
    private val telegramPersonService: TelegramPersonService,
    private val gameService: GameServiceOld,
    private val entriesService: EntriesService,
    private val gameMessageParser: GameMessageParser,
    private val telegramGameMapper: TelegramGameMapper,
) {

    @Transactional
    open fun storeGame(messageMetadata: MessageMetadata): BotApiMethodMessage {
        val game = gameMessageParser.parseCashGame(messageMetadata.text)
        val personIds = telegramPersonService.storePersons(messageMetadata).map { it.id }.toList()
        gameService.storeCashGame(game)
        telegramGameRepository.save(telegramGameMapper.toEntity(game, messageMetadata))
        entriesService.storeBatch(game.id, personIds, game.buyIn, messageMetadata.createdAt)

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            text = "Cash game started."
            replyToMessageId = messageMetadata.id
        }
    }
}
