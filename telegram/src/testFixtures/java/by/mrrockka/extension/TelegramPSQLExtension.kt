package by.mrrockka.extension

import by.mrrockka.repo.ChatGameTable
import by.mrrockka.repo.ChatPersonsTable
import by.mrrockka.repo.PollTaskTable
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.extension.ExtensionContext

class TelegramPSQLExtension : CorePSQLExtension() {
    override fun afterEach(context: ExtensionContext) {
        context.transactionally {
            ChatPersonsTable.deleteAll()
            ChatGameTable.deleteAll()
            PollTaskTable.deleteAll()
        }
    }
}
