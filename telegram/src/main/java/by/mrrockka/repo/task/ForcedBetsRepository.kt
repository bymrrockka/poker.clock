package by.mrrockka.repo.task

import by.mrrockka.domain.ForcedBetsTask
import org.jetbrains.exposed.sql.transactions.transaction

//@Component
class ForcedBetsRepository {

    fun save(task: ForcedBetsTask) {
        transaction {
            TODO()
        }
    }

    fun getAllNotDeleted(): List<ForcedBetsTask> {
        return transaction {
//            todo
            listOf()
        }
    }
}
