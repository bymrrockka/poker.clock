package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.msg.Message

class UpdateBuilder(init: (UpdateBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    internal var message: Message? = null
    internal var editedMessage: Message? = null

    fun message(messageBuilder: (MessageBuilder.() -> Unit) = {}) {
        this.message = MessageBuilder(messageBuilder).message()
    }

    fun message(message: Message) {
        this.message = message
    }

    fun editedMessage(message: Message) {
        this.editedMessage = message
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    fun build(): Update {
        return Update(
                updateId = randoms.updateid(),
                message = message,
//                editedMessage = editedMessage,
        )
    }
}

fun update(init: (UpdateBuilder.() -> Unit) = {}) = UpdateBuilder(init).build()
