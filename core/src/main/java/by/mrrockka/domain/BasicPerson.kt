package by.mrrockka.domain

import java.util.*

interface Person {
    val id: UUID
    val firstname: String?
    val lastname: String?
    val nickname: String?
}

data class BasicPerson(
        override val id: UUID,
        override val firstname: String? = null,
        override val lastname: String? = null,
        override val nickname: String? = null,
) : Person