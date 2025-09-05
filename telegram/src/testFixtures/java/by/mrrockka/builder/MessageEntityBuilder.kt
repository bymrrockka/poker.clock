package by.mrrockka.builder

import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.MetadataEntity
import by.mrrockka.domain.mesageentity.MessageEntityType
import by.mrrockka.domain.mesageentity.MessageEntityType.MENTION
import eu.vendeli.tgbot.types.msg.EntityType

class MessageEntityBuilder(init: (MessageEntityBuilder.() -> Unit) = {}) : AbstractBuilder() {
    internal var domainType: MessageEntityType? = null
    internal var type: EntityType? = null
    internal var messageText: String? = null
    internal var entityText: String? = null

    init {
        randoms(telegramRandoms)
        init()
    }

    fun type(type: MessageEntityType) {
        this.domainType = type
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

    fun domainCommand(): MetadataEntity {
        return MetadataEntity(
                EntityType.BotCommand,
                messageText ?: "",
        )
    }

    fun domainMention(): MetadataEntity {
        return MetadataEntity(
                EntityType.Mention,
                messageText ?: "",
        )
    }

    fun command(): eu.vendeli.tgbot.types.msg.MessageEntity {
        return eu.vendeli.tgbot.types.msg.MessageEntity(
                type = EntityType.BotCommand,
                offset = messageText?.indexOf(entityText ?: "") ?: 0,
                length = entityText?.length ?: 0,
        )
    }

    fun mention(): eu.vendeli.tgbot.types.msg.MessageEntity {
        return eu.vendeli.tgbot.types.msg.MessageEntity(
                type = EntityType.Mention,
                offset = messageText?.indexOf(entityText ?: "") ?: 0,
                length = entityText?.length ?: 0,
        )
    }

}

fun domainMention(init: (@BuilderMarker MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init).also { check(it.domainType == MENTION) }.domainMention()
fun domainCommand(init: (@BuilderMarker MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init).also { check(it.domainType == MENTION) }.domainCommand()
fun List<String>.domainMentions(): List<MetadataEntity> = map { domainMention { messageText(it) } }
fun String?.domainEntities(): List<MetadataEntity> {
    check(this != null) { "text is null" }
    val commandRegex = "^/([\\w]+)".toRegex(RegexOption.MULTILINE)
    val mentionRegex = "^@([\\w]+)".toRegex(RegexOption.MULTILINE)

    val command = commandRegex.find(this.trimIndent())
            .let { match ->
                if (match != null) {
                    val (command) = match.destructured
                    domainCommand { messageText(command) }
                } else null
            }

    val mentions = mentionRegex.findAll(this.trimIndent())
            .map { it.groups[1]!!.value }
            .distinct()
            .toList()
            .domainMentions()

    return (mentions + command).filterNotNull()
}

fun List<String>.mentions(text: String): List<eu.vendeli.tgbot.types.msg.MessageEntity> = map {
    mention {
        messageText(text)
        entityText(it)
    }
}

fun mention(init: (@BuilderMarker MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init)
        .also {
            check(it.messageText != null) { "Message text should present" }
            check(it.entityText != null) { "Entity text should present" }
        }.mention()

fun command(init: (@BuilderMarker MessageEntityBuilder.() -> Unit) = {}) = MessageEntityBuilder(init)
        .also {
            check(it.messageText != null) { "Message text should present" }
            check(it.entityText != null) { "Entity text should present" }
        }.command()

fun String?.entities(): List<eu.vendeli.tgbot.types.msg.MessageEntity> {
    check(this != null) { "text is null" }
//    val text = this.replace(" ", "\n")
    val commandRegex = "^(/[\\w]+)".toRegex(RegexOption.MULTILINE)
    val mentionRegex = "(@[\\w]+)".toRegex(RegexOption.MULTILINE)

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
