package by.mrrockka.extension

import by.mrrockka.repo.ChatGameTable
import by.mrrockka.repo.ChatPersonsTable
import by.mrrockka.repo.ChatPollsTable
import by.mrrockka.repo.PinMessageTable
import by.mrrockka.repo.PollAnswersTable
import by.mrrockka.repo.PollTaskTable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtensionContext

class TelegramPSQLExtension : CorePSQLExtension() {
    override fun afterEach(context: ExtensionContext) {
        transaction {
            ChatPersonsTable.deleteAll()
            ChatGameTable.deleteAll()
            PollAnswersTable.deleteAll()
            ChatPollsTable.deleteAll()
            PollTaskTable.deleteAll()
            PinMessageTable.deleteAll()
            cleanCoreTable()
        }
    }
}
