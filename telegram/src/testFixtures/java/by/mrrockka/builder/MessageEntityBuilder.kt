package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.msg.EntityType
import eu.vendeli.tgbot.types.msg.MessageEntity

class MessageEntityBuilder(init: (MessageEntityBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    internal var type: EntityType? = null
    internal var messageText: String? = null
    internal var entityText: String? = null

    init {
        randoms(telegramRandoms)
        init()
    }

    fun type(type: EntityType) {
        this.type = type
    }

    fun messageText(text: String) {
        this.messageText = text
    }

    fun entityText(text: String) {
        this.entityText = text
    }

    fun command(): MessageEntity {
        return MessageEntity(
                type = EntityType.BotCommand,
                offset = messageText?.indexOf(entityText ?: "") ?: 0,
                length = entityText?.length ?: 0,
        )
    }

    fun mention(): MessageEntity {
        return MessageEntity(
                type = EntityType.Mention,
                offset = messageText?.indexOf(entityText ?: "") ?: 0,
                length = entityText?.length ?: 0,
        )
    }

}

fun List<String>.mentions(text: String): List<MessageEntity> = map {
    mention {
        messageText(text)
        entityText(it)
    }
}

fun mention(init: (MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init)
        .also {
            check(it.messageText != null) { "Message text should present" }
            check(it.entityText != null) { "Entity text should present" }
        }.mention()

fun command(init: (MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init)
        .also {
            check(it.messageText != null) { "Message text should present" }
            check(it.entityText != null) { "Entity text should present" }
        }.command()

fun String?.entities(): List<MessageEntity> {
    check(this != null) { "text is null" }
    val commandRegex = "^(/[\\w]+)".toRegex(RegexOption.MULTILINE)
    val mentionRegex = "(@[\\w]{5,})".toRegex(RegexOption.MULTILINE)

    val command = commandRegex.find(this.trimIndent())
            .let { match ->
                if (match != null) {
                    val (command) = match.destructured
                    command {
                        messageText(this@entities)
                        entityText(command)
                    }
                } else null
            }

    val mentions = mentionRegex.findAll(this.trimIndent())
            .map { it.groups[1]!!.value }
            .distinct()
            .toList()
            .mentions(this)

    return (mentions + command).filterNotNull()
}
