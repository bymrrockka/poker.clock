package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.MetadataEntity
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.msg.Message
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

class MessageBuilder(init: (MessageBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    internal var chatId: Long? = null
    internal var createdAt: Instant? = null
    internal var id: Long? = null
    internal var text: String? = null
    internal var replyToMetadata: MessageMetadata? = null
    internal var replyToMessage: Message? = null
    internal var entities: List<eu.vendeli.tgbot.types.msg.MessageEntity> = mutableListOf()
    internal var domainEntities: List<MetadataEntity> = mutableListOf()
    internal var fromNickname: String? = null
    internal var user: User? = null

    fun chatId(chatId: Long) {
        this.chatId = chatId
    }

    fun createdAt(createdAt: Instant) {
        this.createdAt = createdAt
    }

    fun id(id: Long) {
        this.id = id
    }

    fun text(text: String) {
        this.text = text
    }

    fun replyTo(replyTo: MessageMetadata) {
        this.replyToMetadata = replyTo
    }

    fun replyTo(replyTo: Message) {
        this.replyToMessage = replyTo
    }

    fun fromNickname(fromNickname: String) {
        this.fromNickname = fromNickname
    }

    fun entities(entities: List<String>) {
        this.domainEntities = entities.domainMentions()
    }

    fun entity(entity: MetadataEntity) {
        this.domainEntities + entity
    }

    fun from(userBuilder: (UserBuilder.() -> Unit) = {}) {
        this.user = UserBuilder(userBuilder).build()
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    fun metadata(): MessageMetadata {
        return MessageMetadata(
                id = id ?: randoms.messageid(),
                chatId = randoms.chatid(),
                createdAt = createdAt ?: randoms.instant(),
                text = text ?: randoms.faker.chuckNorris().fact(),
                entities = if (entities.isEmpty()) text.entities() else entities,
                replyTo = replyToMetadata,
                fromNickname = fromNickname,
        )
    }

    @OptIn(ExperimentalTime::class)
    fun message(): Message {
        return Message(
                messageId = id ?: randoms.messageid(),
                chat = chat { id(this@MessageBuilder.chatId) },
                date = (createdAt ?: randoms.instant()).toKotlinInstant(),
                text = text ?: randoms.faker.chuckNorris().fact(),
                replyToMessage = replyToMessage,
                entities = if (entities.isEmpty()) text.entities() else entities,
                from = user ?: user(),
        )
    }
}

fun metadata(init: (MessageBuilder.() -> Unit) = {}) = MessageBuilder(init).metadata()
fun message(init: (MessageBuilder.() -> Unit) = {}) = MessageBuilder(init).message()
