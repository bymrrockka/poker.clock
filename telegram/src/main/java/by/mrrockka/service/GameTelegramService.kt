package by.mrrockka.service

import by.mrrockka.domain.ChatGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.GameMessageParser
import by.mrrockka.repo.ChatGameRepo
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.GameRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GameTelegramService {
    fun storeGame(messageMetadata: MessageMetadata): Game
    fun findGame(messageMetadata: MessageMetadata): ChatGame
}

@Service
@Transactional
open class GameTelegramServiceImpl(
        private val telegramPersonService: TelegramPersonService,
        private val gameRepo: GameRepo,
        private val entriesRepo: EntriesRepo,
        private val chatGameRepo: ChatGameRepo,
        private val gameMessageParser: GameMessageParser,
) : GameTelegramService {

    override fun storeGame(messageMetadata: MessageMetadata): Game {
        check(messageMetadata.mentions.isNotEmpty()) { "Game must have at least one player" }

        val game = gameMessageParser.parse(messageMetadata)
        gameRepo.store(game)
        val personIds = telegramPersonService.findByMessage(messageMetadata)
        entriesRepo.insertBatch(personIds, game, messageMetadata.createdAt)
        chatGameRepo.store(game.id, messageMetadata)

        return game
    }

    override fun findGame(messageMetadata: MessageMetadata): ChatGame {
        val chatGameId = if (messageMetadata.replyTo != null) chatGameRepo.findByMessage(messageMetadata)
        else chatGameRepo.findLatestForChat(messageMetadata)

        check(chatGameId != null) { "Game was not found for the chat." }

        return ChatGame(game = gameRepo.findById(chatGameId), messageMetadata = messageMetadata)
    }

}
