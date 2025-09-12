package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.EntryMessageParser
import by.mrrockka.repo.EntriesRepo
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface EntryTelegramService {
    fun entry(metadata: MessageMetadata): Pair<Set<String>, BigDecimal>
}

@Service
class EntryTelegramServiceImpl(
        private val entriesRepo: EntriesRepo,
        private val entryMessageParser: EntryMessageParser,
        private val gameTelegramService: GameTelegramService,
        private val telegramPersonService: TelegramPersonService,
) : EntryTelegramService {

    override fun entry(metadata: MessageMetadata): Pair<Set<String>, BigDecimal> {
        metadata.checkMentions()
        //todo: add ability to entry without nickname or @me and decline command handler
        val (nicknames, amount) = entryMessageParser.parse(metadata)
        val telegramGame = gameTelegramService.findGame(metadata)
        val personIds = telegramPersonService.findByMessage(metadata).map { it.id }
        entriesRepo.insertBatch(personIds, (amount ?: telegramGame.game.buyIn), telegramGame.game, metadata.createdAt)

        return nicknames to (amount ?: telegramGame.game.buyIn)
    }
}
