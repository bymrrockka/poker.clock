package by.mrrockka.extension

import by.mrrockka.extension.TestPSQLContainer.Companion.container
import by.mrrockka.extension.TestPSQLContainer.Companion.dbName
import by.mrrockka.extension.TestPSQLContainer.Companion.password
import by.mrrockka.extension.TestPSQLContainer.Companion.username
import by.mrrockka.extension.TestPSQLContainer.Companion.version
import by.mrrockka.repo.BountyTable
import by.mrrockka.repo.EntriesTable
import by.mrrockka.repo.FinalePlacesTable
import by.mrrockka.repo.GameTable
import by.mrrockka.repo.MoneyTransferTable
import by.mrrockka.repo.PersonTable
import by.mrrockka.repo.PrizePoolTable
import by.mrrockka.repo.WithdrawalTable
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate

open class CorePSQLExtension : BeforeAllCallback, AfterEachCallback {
    override fun beforeAll(context: ExtensionContext?) {
        container.start()
        System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:$version:///%$dbName")
        System.setProperty("spring.datasource.username", username)
        System.setProperty("spring.datasource.password", password)
    }

    override fun afterEach(context: ExtensionContext) {
        val template = SpringExtension.getApplicationContext(context)
                .getBean(TransactionTemplate::class.java)
        template.setPropagationBehaviorName("PROPAGATION_REQUIRED")

        template.execute {
            cleanCoreTable()
        }
    }

    protected fun cleanCoreTable() {
        MoneyTransferTable.deleteAll()
        BountyTable.deleteAll()
        EntriesTable.deleteAll()
        WithdrawalTable.deleteAll()
        PrizePoolTable.deleteAll()
        FinalePlacesTable.deleteAll()
        PersonTable.deleteAll()
        GameTable.deleteAll()
    }

}
