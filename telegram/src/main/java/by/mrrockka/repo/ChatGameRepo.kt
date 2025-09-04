package by.mrrockka.repo

import by.mrrockka.domain.MessageMetadata
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.time.ExperimentalTime

interface ChatGameRepo {
    fun findByMessage(messageMetadata: MessageMetadata): UUID?
    fun findLatestForChat(messageMetadata: MessageMetadata): UUID?
    fun store(gameId: UUID, message: MessageMetadata)
}

@OptIn(ExperimentalTime::class)
@Repository
@Transactional
open class DatabaseChatGameRepo : ChatGameRepo {
    override fun findByMessage(messageMetadata: MessageMetadata): UUID? {
        return ChatGameTable.select(ChatGameTable.gameId)
                .where {
                    (ChatGameTable.messageId eq messageMetadata.replyTo!!.id) and
                            (ChatGameTable.chatId eq messageMetadata.chatId)
                }
                .map { it[ChatGameTable.gameId] }
                .firstOrNull()
    }

    override fun findLatestForChat(messageMetadata: MessageMetadata): UUID? {
        return ChatGameTable.selectAll()
                .where { (ChatGameTable.chatId eq messageMetadata.chatId) }
                .orderBy(ChatGameTable.createdAt to SortOrder.DESC)
                .map { it[ChatGameTable.gameId] }
                .firstOrNull()
    }

    override fun store(gameId: UUID, message: MessageMetadata) {
        ChatGameTable.insert {
            it[ChatGameTable.chatId] = message.chatId
            it[ChatGameTable.gameId] = gameId
            it[ChatGameTable.messageId] = message.id
            it[ChatGameTable.createdAt] = message.createdAt
        }
    }
}