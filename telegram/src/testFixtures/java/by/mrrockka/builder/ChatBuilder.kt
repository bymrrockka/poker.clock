package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.chat.Chat
import eu.vendeli.tgbot.types.chat.ChatType

class ChatBuilder(init: (ChatBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    internal var id: Long? = null
    fun id(id: Long?) {
        this.id = id
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    fun build(): Chat {
        return Chat(
                id = id ?: randoms.chatid(),
                type = ChatType.Group,
        )
    }
}

fun chat(init: (ChatBuilder.() -> Unit) = {}) = ChatBuilder(init).build()
