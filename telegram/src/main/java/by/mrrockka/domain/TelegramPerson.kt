package by.mrrockka.domain

import java.util.*

class TelegramPerson(
        override val id: UUID,
        override val firstname: String?,
        override val lastname: String?,
        override val nickname: String,
        var chatId: Long,
) : Person {
    //    todo: remove
    companion object {
        @JvmStatic
        fun telegramPersonBuilder(): TelegramPersonBuilder = TelegramPersonBuilder()
    }

    @Deprecated(message = "Use constructor instead.")
    class TelegramPersonBuilder {
        lateinit var id: UUID
        var chatId: Long = -1L
        var firstname: String? = null
        var lastname: String? = null
        lateinit var nickname: String

        fun id(id: UUID): TelegramPersonBuilder {
            this.id = id; return this
        }

        fun firstname(firstname: String): TelegramPersonBuilder {
            this.firstname = firstname; return this
        }

        fun lastname(lastname: String): TelegramPersonBuilder {
            this.lastname = lastname; return this
        }

        fun nickname(nickname: String): TelegramPersonBuilder {
            this.nickname = nickname; return this
        }

        fun chatId(chatId: Long): TelegramPersonBuilder {
            this.chatId = chatId; return this
        }

        fun build(): TelegramPerson {
            check(chatId != -1L) { "ChatId must be set" }
            return TelegramPerson(id, firstname, lastname, nickname, chatId)
        }
    }
}