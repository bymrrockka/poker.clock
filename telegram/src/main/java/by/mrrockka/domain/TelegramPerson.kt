package by.mrrockka.domain

import java.util.*

data class TelegramPerson(
        override val id: UUID,
        override val firstname: String?,
        override val lastname: String?,
        override val nickname: String,
        var chatId: Long,
) : Person