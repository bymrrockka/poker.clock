package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.mesageentity.MessageEntity
import by.mrrockka.domain.mesageentity.MessageEntityType
import by.mrrockka.domain.mesageentity.MessageEntityType.BOT_COMMAND
import by.mrrockka.domain.mesageentity.MessageEntityType.MENTION
import org.telegram.telegrambots.meta.api.objects.User

//TODO: refactor
@Suppress("UNCHECKED_CAST")
class MessageEntityBuilder(init: (MessageEntityBuilder.() -> Unit) = {}) {
    var randoms = telegramRandoms
    var type: MessageEntityType? = null
    var text: String? = null
    var user: User? = null

    init {
        init()
    }

    fun command(): MessageEntity {
        return MessageEntity(
                BOT_COMMAND,
                text ?: "",
                user
        )
    }

    fun mention(): MessageEntity {
        return MessageEntity(
                MENTION,
                text ?: "",
                user
        )
    }

}

fun messageEntity(init: (MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init)
fun List<String>.mentions(): List<MessageEntity> = map { messageEntity { this.text = it }.mention() }
fun messageEntity(randoms: TelegramRandoms, init: (MessageEntityBuilder.() -> Unit) = {}) = messageEntity { this.randoms = randoms }.also(init)
