package by.mrrockka.service.game

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TelegramGame
import by.mrrockka.mapper.TelegramGameMapper
import by.mrrockka.repo.game.TelegramGameRepository
import by.mrrockka.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import java.util.*

@Service
open class GameTelegramFacadeService(
        private val telegramGameRepository: TelegramGameRepository,
        private val gameService: GameService,
        private val telegramGameMapper: TelegramGameMapper,
) {
    @Autowired
    lateinit private var tournamentGameService: TournamentGameService

    @Autowired
    lateinit private var cashGameService: CashGameService

    @Autowired
    lateinit private var bountyGameService: BountyGameService

    fun storeTournamentGame(messageMetadata: MessageMetadata): BotApiMethodMessage {
        return tournamentGameService.storeGame(messageMetadata)
    }

    fun storeCashGame(messageMetadata: MessageMetadata): BotApiMethodMessage {
        return cashGameService.storeGame(messageMetadata)
    }

    fun storeBountyGame(messageMetadata: MessageMetadata): BotApiMethodMessage {
        return bountyGameService.storeGame(messageMetadata)
    }

    fun getGameByMessageMetadata(messageMetadata: MessageMetadata): Optional<TelegramGame> {
//        todo: refactor to kotlin -> remove optionals
        return messageMetadata.optReplyTo()
                .map { telegramGameRepository.findByChatAndMessageId(messageMetadata.chatId, it.id) }
                .orElseGet { telegramGameRepository.findLatestByChatId(messageMetadata.chatId) }
                .map { telegramGameMapper.toGame(gameService.retrieveGame(it.gameId), it) }
    }
}
