package by.mrrockka.repo

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ChatPersonsRepo {
    fun store(personIds: List<UUID>, chatId: Long)
    fun personChats(personId: UUID): List<Long>
}

@Repository
@Transactional
open class ChatPersonsRepoImpl : ChatPersonsRepo {

    override fun store(personIds: List<UUID>, chatId: Long) {
        ChatPersonsTable.batchInsert(personIds, ignore = true) { id ->
            this[ChatPersonsTable.chatId] = chatId
            this[ChatPersonsTable.personId] = id
        }
    }

    override fun personChats(personId: UUID): List<Long> {
        return ChatPersonsTable.select(ChatPersonsTable.chatId)
                .where(ChatPersonsTable.personId eq personId)
                .map { it[ChatPersonsTable.chatId] }
                .toList()
    }
}