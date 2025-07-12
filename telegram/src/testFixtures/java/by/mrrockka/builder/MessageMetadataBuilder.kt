package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.mesageentity.MessageEntity
import java.time.Instant

@Suppress("UNCHECKED_CAST")
class MessageMetadataBuilder(init: (MessageMetadataBuilder.() -> Unit) = {}) {

    var randoms = telegramRandoms
    var chatId: Long? = null
    var createdAt: Instant? = null
    var id: Int? = null
    var text: String? = null
    var replyTo: MessageMetadata? = null
    var entities: List<MessageEntity> = mutableListOf()
    var fromNickname: String? = null

    init {
        init()
    }

    fun entity(entity: MessageEntity): MessageMetadataBuilder {
        this.entities + entity
        return this
    }

    fun build(): MessageMetadata {
        return MessageMetadata(
                id = id ?: randoms.messageid(),
                chatId = randoms.chatid(),
                createdAt = createdAt ?: randoms.instant(),
                text = text ?: randoms.faker.chuckNorris().fact(),
                entities = this.entities,
                replyTo = replyTo,
                fromNickname = fromNickname
        )
    }
}

fun message(init: (MessageMetadataBuilder.() -> Unit) = {}) = MessageMetadataBuilder(init).build()
fun message(randoms: TelegramRandoms, init: (MessageMetadataBuilder.() -> Unit) = {}) = MessageMetadataBuilder { this.randoms = randoms }.also(init).build()
