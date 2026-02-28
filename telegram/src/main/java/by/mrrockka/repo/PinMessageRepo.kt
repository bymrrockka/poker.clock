package by.mrrockka.repo

import eu.vendeli.tgbot.types.msg.Message
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface PinMessageRepo {
    fun store(message: Message, type: PinType)
    fun selectByChat(chatId: Long, type: PinType): List<Long>
    fun delete(messageIds: List<Long>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PinMessageRepoImpl : PinMessageRepo {

    override fun store(message: Message, type: PinType) {
        PinMessageTable.insert {
            it[PinMessageTable.chatId] = message.chat.id
            it[PinMessageTable.messageId] = message.messageId
            it[PinMessageTable.type] = type
        }
    }

    override fun selectByChat(chatId: Long, type: PinType): List<Long> {
        return PinMessageTable.selectAll()
                .where { (PinMessageTable.chatId eq chatId) and (PinMessageTable.type eq type) }
                .map { it[PinMessageTable.messageId] }
    }

    override fun delete(messageIds: List<Long>) {
        PinMessageTable.deleteWhere { PinMessageTable.messageId inList messageIds }
    }

}

enum class PinType {
    POLL, GAME
}