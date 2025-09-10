package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.EntryMessageParser
import by.mrrockka.repo.EntriesRepo
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@RequiredArgsConstructor
class EntryTelegramService(
        private val entriesRepo: EntriesRepo,
        private val entryMessageParser: EntryMessageParser,
        private val gameTelegramService: GameTelegramService,
        private val telegramPersonService: TelegramPersonService,
) {

    fun entry(metadata: MessageMetadata): Pair<Set<String>, BigDecimal> {
        metadata.checkMentions()
        //todo: add ability to entry without nickname or @me and decline command handler
        val (nicknames, amount) = entryMessageParser.parse(metadata)
        val telegramGame = gameTelegramService.findGame(metadata)
        val personIds = telegramPersonService.findByMessage(metadata).map { it.id }
        entriesRepo.insertBatch(personIds, (amount ?: telegramGame.game.buyIn), telegramGame.game, metadata.createdAt)

        return nicknames to (amount ?: telegramGame.game.buyIn)
    }
}
