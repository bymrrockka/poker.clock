package by.mrrockka.repo

import by.mrrockka.domain.MessageMetadata
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class ChatGameRepo {

    fun findByMessage(messageMetadata: MessageMetadata): UUID? {
        return ChatGameTable.select(ChatGameTable.gameId)
                .where {
                    (ChatGameTable.messageId eq messageMetadata.replyTo!!.id) and
                            (ChatGameTable.chatId eq messageMetadata.chatId)
                }
                .map { it[ChatGameTable.gameId] }
                .firstOrNull()
    }

    fun findLatestByMessage(messageMetadata: MessageMetadata): UUID? {
        return ChatGameTable.selectAll()
                .where { (ChatGameTable.messageId eq messageMetadata.id) and (ChatGameTable.chatId eq messageMetadata.chatId) }
                .map { it[ChatGameTable.gameId] }
                .firstOrNull()
    }
}