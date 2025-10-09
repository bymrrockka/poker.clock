package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.GameMessageParser
import by.mrrockka.repo.ChatGameRepo
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.GameRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface GameTelegramService {
    fun store(metadata: MessageMetadata): Game
    fun findGame(metadata: MessageMetadata): Game
    fun findByChat(metadata: MessageMetadata): List<Game>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class GameTelegramServiceImpl(
        private val personService: TelegramPersonService,
        private val gameRepo: GameRepo,
        private val entriesRepo: EntriesRepo,
        private val chatGameRepo: ChatGameRepo,
        private val pollService: PollTelegramService,
        private val gameMessageParser: GameMessageParser,
) : GameTelegramService {

    override fun store(metadata: MessageMetadata): Game {
        val game = gameMessageParser.parse(metadata)
        gameRepo.store(game)
        chatGameRepo.store(game.id, metadata)

        if (metadata.replyTo?.poll != null) {
            val excludes = if (metadata.mentions.isNotEmpty())
                personService.findByMessage(metadata).map { it.id }
            else emptyList()

            pollService.findParticipants(metadata.replyTo.poll.id)
                    .filterNot { excludes.contains(it) }
                    .also { personIds ->
                        entriesRepo.store(personIds, game.buyIn, game, metadata.createdAt)
                    }
        } else {
            metadata.checkMentions()
            personService.findByMessage(metadata).map { it.id }
                    .also { personIds ->
                        entriesRepo.store(personIds, game.buyIn, game, metadata.createdAt)
                    }
        }

        return game
    }

    override fun findGame(metadata: MessageMetadata): Game {
        val chatGameId = if (metadata.replyTo != null) chatGameRepo.findByMessage(metadata)
        else chatGameRepo.findLatestForChat(metadata)

        check(chatGameId != null) { "Game was not found for the chat." }

        return gameRepo.findById(chatGameId)
    }

    override fun findByChat(metadata: MessageMetadata): List<Game> {
        return gameRepo.findByIds(chatGameRepo.findByChat(metadata))
    }
}
