package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
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
    internal var replyToMessage: Message? = null
    internal var entities: List<eu.vendeli.tgbot.types.msg.MessageEntity> = mutableListOf()
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

    fun replyTo(replyTo: Message) {
        this.replyToMessage = replyTo
    }

    fun replyTo(messageBuilder: (MessageBuilder.() -> Unit) = {}) {
        this.replyToMessage = MessageBuilder(messageBuilder).message()
    }

    fun from(userBuilder: (UserBuilder.() -> Unit) = {}) {
        this.user = UserBuilder(userBuilder).build()
    }

    fun from(user: User) {
        this.user = user
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    @OptIn(ExperimentalTime::class)
    fun message(): Message {
        text = text ?: randoms.faker.chuckNorris().fact()
        return Message(
                messageId = id ?: randoms.messageid(),
                chat = chat { id(this@MessageBuilder.chatId) },
                date = (createdAt ?: randoms.instant()).toKotlinInstant(),
                text = text,
                replyToMessage = replyToMessage,
                entities = if (entities.isEmpty()) text.entities() else entities,
                from = user ?: user(),
        )
    }
}

fun message(init: (MessageBuilder.() -> Unit) = {}) = MessageBuilder(init).message()
