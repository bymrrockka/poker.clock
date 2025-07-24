package by.mrrockka.service.game

import by.mrrockka.domain.*
import by.mrrockka.parser.GameMessageParser
import by.mrrockka.repo.ChatGameRepo
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.GameRepo
import by.mrrockka.service.TelegramPersonService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

interface GameTelegramService {
    fun storeGame(messageMetadata: MessageMetadata): BotApiMethodMessage
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


    override fun storeGame(messageMetadata: MessageMetadata): BotApiMethodMessage {
        check(messageMetadata.mentions().isNotEmpty()) { "Game must have at least one player" }

        val game = gameMessageParser.parse(messageMetadata)
        gameRepo.save(game)
        val personIds = telegramPersonService.findByMentions(messageMetadata)
        entriesRepo.insertBatch(personIds, game, messageMetadata.createdAt)
        chatGameRepo.store(game.id, messageMetadata)

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            text = game.responseMessage()
            replyToMessageId = messageMetadata.id
        }
    }

    override fun findGame(messageMetadata: MessageMetadata): ChatGame {
        val chatGameId = if (messageMetadata.replyTo != null) chatGameRepo.findByMessage(messageMetadata)
        else chatGameRepo.findLatestForChat(messageMetadata)

        check(chatGameId != null) { "Game was not found for the chat." }

        return ChatGame(game = gameRepo.findById(chatGameId), messageMetadata = messageMetadata)
    }

    private fun Game.responseMessage(): String =
            when (this) {
                is CashGame -> "Cash game started."
                is TournamentGame -> "Tournament game started."
                is BountyTournamentGame -> "Bounty tournament game started."
                else -> error("Game type not supported: ${this.javaClass.simpleName}")
            }

}
