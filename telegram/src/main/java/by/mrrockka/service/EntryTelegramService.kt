package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Table
import by.mrrockka.parser.EntryMessageParser
import by.mrrockka.repo.EntriesRepo
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface EntryTelegramService {
    fun entry(metadata: MessageMetadata): Pair<List<Table>, BigDecimal>
}

@Service
class EntryTelegramServiceImpl(
        private val entriesRepo: EntriesRepo,
        private val entryMessageParser: EntryMessageParser,
        private val gameService: GameTelegramService,
        private val personService: TelegramPersonService,
        private val tablesService: GameTablesService,
) : EntryTelegramService {

    override fun entry(metadata: MessageMetadata): Pair<List<Table>, BigDecimal> {
        metadata.checkMentions()
        val amount = entryMessageParser.parse(metadata)
        val game = gameService.findGame(metadata)
        val persons = personService.findByMessage(metadata)
        entriesRepo.store(persons.map { it.id }, (amount ?: game.buyIn), game, metadata.createdAt)
        val tables = tablesService.entries(game, persons)

        return tables to (amount ?: game.buyIn)
    }
}
