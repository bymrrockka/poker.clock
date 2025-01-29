package by.mrrockka.creator

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update


class UpdateCreator private constructor() {
    companion object {
        var updateId = 0

        fun update(message: Message?): Update {
            return update { this.message = message }
        }

        fun update(block: (Update.() -> Unit) = {}): Update {
            val update = Update()
            update.updateId = ++updateId

            return update.apply(block)
        }

        fun emptyList(): List<Update> = listOf(update())
    }
}
