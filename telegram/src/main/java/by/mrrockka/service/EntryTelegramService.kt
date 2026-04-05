package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Table
import by.mrrockka.parser.EntryMessageParser
import by.mrrockka.repo.ChatMessagesRepo
import by.mrrockka.repo.CommandType
import by.mrrockka.repo.EntriesRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface EntryTelegramService {
    fun entry(metadata: MessageMetadata): Pair<List<Table>, BigDecimal>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class EntryTelegramServiceImpl(
        private val entriesRepo: EntriesRepo,
        private val entryMessageParser: EntryMessageParser,
        private val gameService: GameTelegramService,
        private val personService: TelegramPersonService,
        private val tablesService: GameTablesService,
        private val chatMessagesRepo: ChatMessagesRepo,
) : EntryTelegramService {

    override fun entry(metadata: MessageMetadata): Pair<List<Table>, BigDecimal> {
        check(metadata.mentions.isEmpty()) { "Entry command does not require mentions anymore. Use as /entry" }
        check(metadata.from != null) { "Only users can enter game" }
        check(metadata.from.username != null) { "User must have nickname to execute command" }
        val amount = entryMessageParser.parse(metadata)
        val game = gameService.findGame(metadata)
        val person = personService.findOrAdd(metadata.from.username!!, metadata.chatId)

        val operationId = entriesRepo.store(game.id, person.id, (amount ?: game.buyIn), metadata.createdAt)
        chatMessagesRepo.store(metadata, operationId, CommandType.ENTRY)
        val tables = tablesService.entry(game, person)

        return tables to (amount ?: game.buyIn)
    }
}
