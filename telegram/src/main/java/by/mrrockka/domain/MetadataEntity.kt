package by.mrrockka.domain

import eu.vendeli.tgbot.types.msg.EntityType

data class MetadataEntity(val type: EntityType, val text: String) {

    companion object {
        @JvmStatic
        fun builder(): MessageEntityBuilder = MessageEntityBuilder()
    }

    @Deprecated(message = "Use constructor instead.")
    class MessageEntityBuilder {
        lateinit var type: EntityType
        lateinit var text: String

        fun type(type: EntityType): MessageEntityBuilder {
            this.type = type
            return this
        }

        fun text(text: String): MessageEntityBuilder {
            this.text = text
            return this
        }

        fun build(): MetadataEntity {
            return MetadataEntity(
                type = type,
                text = text
            )
        }
    }
}