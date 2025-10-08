package by.mrrockka.repo

import eu.vendeli.tgbot.types.msg.Message
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface PinMessageRepo {
    fun store(message: Message, type: PinType)
    fun selectByChat(chatId: Long, type: PinType): List<Long>
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
        return PinMessageTable.select(PinMessageTable.messageId)
                .where { (PinMessageTable.chatId eq chatId) and (PinMessageTable.type eq type) }
                .map { it[PinMessageTable.messageId] }
    }

}

enum class PinType {
    POLL, GAME
}