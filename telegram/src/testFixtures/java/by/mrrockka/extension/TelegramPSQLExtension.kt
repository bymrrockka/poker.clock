package by.mrrockka.extension

import by.mrrockka.repo.ChatGameTable
import by.mrrockka.repo.ChatPersonsTable
import by.mrrockka.repo.ChatPollsTable
import by.mrrockka.repo.GameSeatsTable
import by.mrrockka.repo.PinMessageTable
import by.mrrockka.repo.PollAnswersTable
import by.mrrockka.repo.PollTaskTable
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate

class TelegramPSQLExtension : CorePSQLExtension() {
    override fun afterEach(context: ExtensionContext) {
        val template = SpringExtension.getApplicationContext(context)
                .getBean(TransactionTemplate::class.java)

        template.execute {
            PinMessageTable.deleteAll()
            ChatPersonsTable.deleteAll()
            ChatGameTable.deleteAll()
            PollAnswersTable.deleteAll()
            ChatPollsTable.deleteAll()
            PollTaskTable.deleteAll()
            GameSeatsTable.deleteAll()
            cleanCoreTable()
        }
    }
}
