package by.mrrockka.repo

import org.jetbrains.exposed.sql.batchInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ChatPersonsRepo {
    fun insertBatch(personIds: List<UUID>, chatId: Long)
}

@Repository
@Transactional
open class ChatPersonsRepoImpl : ChatPersonsRepo {

    override fun insertBatch(personIds: List<UUID>, chatId: Long) {
        ChatPersonsTable.batchInsert(personIds, ignore = true) { id ->
            this[ChatPersonsTable.chatId] = chatId
            this[ChatPersonsTable.personId] = id
        }
    }
}