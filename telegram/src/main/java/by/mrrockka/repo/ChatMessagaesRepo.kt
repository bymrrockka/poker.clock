package by.mrrockka.repo

import by.mrrockka.domain.MessageMetadata
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface ChatMessagesRepo {
    fun store(message: MessageMetadata, operationId: Int, type: CommandType)
    fun getOperationId(chatId: Long, messageId: Long): Int?
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class ChatMessagesRepoImpl : ChatMessagesRepo {

    override fun store(message: MessageMetadata, operationId: Int, type: CommandType) {
        ChatMessagesTable.insert {
            it[ChatMessagesTable.messageId] = message.id
            it[ChatMessagesTable.chatId] = message.chatId
            it[ChatMessagesTable.createdAt] = message.createdAt
            it[ChatMessagesTable.type] = type
            it[ChatMessagesTable.operationId] = operationId
        }

    }

    override fun getOperationId(chatId: Long, messageId: Long): Int? {
        return ChatMessagesTable.select(ChatMessagesTable.operationId)
                .where { (ChatMessagesTable.chatId eq chatId) and (ChatMessagesTable.messageId eq messageId) }
                .map { it[ChatMessagesTable.operationId] }
                .firstOrNull()
    }

}

enum class CommandType {
    ENTRY, WITHDRAWAL, BOUNTY
}