package by.mrrockka.repo

import org.jetbrains.exposed.sql.batchUpsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class ChatPersonsRepo {

    fun upsertBatch(personIds: List<UUID>, chatId: Long) {
        ChatPersonsTable.batchUpsert(personIds) { id ->
            this[ChatPersonsTable.chatId] = chatId
            this[ChatPersonsTable.personId] = id
        }
    }
}