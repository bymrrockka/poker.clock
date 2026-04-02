package by.mrrockka.repo

import by.mrrockka.domain.MessageMetadata
import org.jetbrains.exposed.v1.jdbc.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Clock
import kotlin.time.toJavaInstant

interface ChatMessagesRepo {
    fun upsert(message: MessageMetadata, type: CommandType)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class ChatMessagesRepoImpl(
        private val clock: Clock,
) : ChatMessagesRepo {

    override fun upsert(message: MessageMetadata, type: CommandType) {
        ChatMessagesTable.upsert(
                keys = arrayOf(ChatMessagesTable.chatId, ChatMessagesTable.messageId),
                onUpdate = {
                    it[ChatMessagesTable.updatedAt] = clock.now().toJavaInstant()
                },
        ) {
            it[ChatMessagesTable.messageId] = message.id
            it[ChatMessagesTable.chatId] = message.chatId
            it[ChatMessagesTable.createdAt] = message.createdAt
            it[ChatMessagesTable.type] = type
        }

    }

}

enum class CommandType {
    ENTRY, WITHDRAWAL, BOUNTY
}