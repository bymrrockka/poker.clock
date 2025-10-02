package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.msg.Message
import eu.vendeli.tgbot.types.msg.MessageEntity
import eu.vendeli.tgbot.types.poll.Poll
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

class MessageBuilder(init: (MessageBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    private var chatId: Long? = null
    private var createdAt: Instant? = null
    private var id: Long? = null
    private var text: String? = null
    private var replyToMessage: Message? = null
    private var entities: List<MessageEntity> = mutableListOf()
    private var user: User? = null
    private var poll: Poll? = null

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

    fun poll(pollBuilder: (PollBuilder.() -> Unit) = {}) {
        this.poll = PollBuilder(pollBuilder).poll()
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    @OptIn(ExperimentalTime::class)
    fun message(): Message {
        return Message(
                messageId = id ?: randoms.messageid(),
                chat = chat { id(this@MessageBuilder.chatId) },
                date = (createdAt ?: randoms.instant()).toKotlinInstant(),
                text = text ?: randoms.faker.chuckNorris().fact(),
                replyToMessage = replyToMessage,
                entities = if (entities.isEmpty() && text != null) text.entities() else entities,
                from = user ?: user(),
                poll = poll,
        )
    }
}

fun message(init: (MessageBuilder.() -> Unit) = {}) = MessageBuilder(init).message()
