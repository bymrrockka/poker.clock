package by.mrrockka.domain

import by.mrrockka.domain.mesageentity.MessageEntity
import by.mrrockka.domain.mesageentity.MessageEntityType
import java.time.Instant
import java.util.*
import java.util.stream.Stream

//todo check if it's needed
data class MessageMetadata(
        val chatId: Long,
        val createdAt: Instant,
        val id: Int,
        val text: String,
        val replyTo: MessageMetadata?,
        val entities: List<MessageEntity>,
        val fromNickname: String?,
) {
    fun optFromNickname(): Optional<String> {
        return Optional.ofNullable(fromNickname)
    }

    @Deprecated("Used by old java code")
    fun mentionsStream(): Stream<MessageEntity> {
        return entities.stream()
                .filter { entity -> entity.type == MessageEntityType.MENTION }
    }

    fun mentions(): List<MessageEntity> {
        return entities.filter { entity -> entity.type == MessageEntityType.MENTION }
    }

    fun command(): MessageEntity {
        return entities
                .find { entity -> entity.type == MessageEntityType.BOT_COMMAND }
                ?: error("Message has no command.")
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
        var id: Int = -1
        lateinit var text: String
        var replyTo: MessageMetadata? = null
        lateinit var entities: List<MessageEntity>
        var fromNickname: String? = null

        fun id(id: Int): MessageMetadataBuilder {
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

        fun entities(entities: List<MessageEntity>): MessageMetadataBuilder {
            this.entities = entities; return this
        }

        fun fromNickname(fromNickname: String?): MessageMetadataBuilder {
            this.fromNickname = fromNickname; return this
        }

        fun build(): MessageMetadata {
            check(chatId != -1L) { "chatId must be set" }
            check(id != -1) { "id must be set" }

            return MessageMetadata(
                    chatId = chatId,
                    createdAt = createdAt,
                    id = id,
                    text = text,
                    replyTo = replyTo,
                    entities = entities,
                    fromNickname = fromNickname,
            )
        }
    }
}
