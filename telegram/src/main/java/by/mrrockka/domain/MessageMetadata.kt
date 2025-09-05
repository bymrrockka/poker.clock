package by.mrrockka.domain

import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.msg.EntityType
import eu.vendeli.tgbot.types.msg.Message
import eu.vendeli.tgbot.types.msg.MessageEntity
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

data class MessageMetadata(
        val chatId: Long,
        val createdAt: Instant,
        val id: Long,
        val text: String,
        val replyTo: MessageMetadata?,
        val entities: List<MessageEntity>,
        val metadataEntities: List<MetadataEntity>? = null,
        val fromNickname: String? = null,
        val from: User? = null,
) {

    val command: MetadataEntity by lazy {
        //todo: fr backwards compatibility
        metadataEntities?.find { entity -> entity.type == EntityType.BotCommand }
                ?: entities.find { entity -> entity.type == EntityType.BotCommand }?.toMetadata(text)
                ?: error("Message does not contain command")
    }

    val mentions: Set<MetadataEntity> by lazy {
        //todo: fr backwards compatibility
        if (!metadataEntities.isNullOrEmpty()) {
            metadataEntities
                    .filter { entity -> entity.type == EntityType.Mention }
                    .toSet()
        } else
            entities
                    .filter { entity -> entity.type == EntityType.Mention }
                    .map { it.toMetadata(text) }
                    .toSet()
    }

    private fun MessageEntity.toMetadata(text: String): MetadataEntity {
        return MetadataEntity(
                text = text.substring(offset + 1, offset + length),
                type = type,
        )
    }

    //todo: remove
    companion object {
        @JvmStatic
        fun builder(): MessageMetadataBuilder = MessageMetadataBuilder()
    }

    @Deprecated(message = "Use constructor instead.")
    class MessageMetadataBuilder {
        var chatId: Long = -1L
        lateinit var createdAt: Instant
        var id: Long = -1L
        lateinit var text: String
        var replyTo: MessageMetadata? = null
        lateinit var entities: List<MetadataEntity>
        var fromNickname: String? = null

        fun id(id: Long): MessageMetadataBuilder {
            this.id = id; return this
        }

        fun chatId(chatId: Long): MessageMetadataBuilder {
            this.chatId = chatId; return this
        }

        fun createdAt(createdAt: Instant): MessageMetadataBuilder {
            this.createdAt = createdAt; return this
        }

        fun text(text: String): MessageMetadataBuilder {
            this.text = text; return this
        }

        fun replyTo(replyTo: MessageMetadata?): MessageMetadataBuilder {
            this.replyTo = replyTo; return this
        }

        fun metadataEntities(entities: List<MetadataEntity>): MessageMetadataBuilder {
            this.entities = entities; return this
        }

        fun fromNickname(fromNickname: String?): MessageMetadataBuilder {
            this.fromNickname = fromNickname; return this
        }

        @OptIn(ExperimentalTime::class)
        fun build(): MessageMetadata {
            check(chatId != -1L) { "chatId must be set" }
            check(id != -1L) { "id must be set" }

            return MessageMetadata(
                    chatId = chatId,
                    createdAt = createdAt,
                    id = id,
                    text = text,
                    replyTo = replyTo,
                    metadataEntities = entities,
                    entities = emptyList(),
                    fromNickname = fromNickname,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
fun Message.toMessageMetadata(): MessageMetadata =
        MessageMetadata(
                chatId = chat.id,
                createdAt = this.date.toJavaInstant(),
                id = this.messageId,
                text = this.text.orEmpty(),
                replyTo = this.replyToMessage?.toMessageMetadata(),
                entities = this.entities ?: emptyList(),
                from = from,
        )
