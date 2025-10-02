package by.mrrockka.domain

import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.msg.EntityType
import eu.vendeli.tgbot.types.msg.Message
import eu.vendeli.tgbot.types.msg.MessageEntity
import eu.vendeli.tgbot.types.poll.Poll
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

data class MessageMetadata(
        val id: Long,
        val chatId: Long,
        val text: String,
        val entities: List<MessageEntity>,
        val from: User? = null,
        val replyTo: MessageMetadata?,
        val createdAt: Instant,
        val poll: Poll?,
) {

    val command: MetadataEntity by lazy {
        entities.find { entity -> entity.type == EntityType.BotCommand }?.toMetadata(text)
                ?: error("Message does not contain command")
    }

    val mentions: Set<MetadataEntity> by lazy {
        entities.filter { entity -> entity.type == EntityType.Mention }
                .map { it.toMetadata(text) }
                .toSet()
                .let {
                    //@me mention entity adjustment
                    val hasMe = "(@me(\\W|$))".toRegex(RegexOption.MULTILINE).containsMatchIn(this@MessageMetadata.text)
                    if (hasMe) {
                        val username = this.from?.username ?: error("User doesn't have username")
                        it + MetadataEntity(text = username, type = EntityType.Mention)
                    } else it
                }
    }

    private fun MessageEntity.toMetadata(text: String): MetadataEntity {
        return MetadataEntity(
                text = text.substring(offset + 1, offset + length),
                type = type,
        )
    }
}

data class MetadataEntity(val type: EntityType, val text: String)

@OptIn(ExperimentalTime::class)
fun Message.toMessageMetadata(): MessageMetadata =
        MessageMetadata(
                chatId = chat.id,
                createdAt = date.toJavaInstant(),
                id = messageId,
                text = text.orEmpty(),
                replyTo = replyToMessage?.toMessageMetadata(),
                entities = entities ?: emptyList(),
                from = from,
                poll = poll
        )
