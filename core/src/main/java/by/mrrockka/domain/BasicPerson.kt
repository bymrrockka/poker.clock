package by.mrrockka.domain

import java.util.*

interface Person {
    val id: UUID
}

data class BasicPerson(
        override val id: UUID,
        val nickname: String? = null,
) : Person

data class ServiceFee(
        val description: String,
        val url: String,
) : Person {
    override val id: UUID = UUID.randomUUID()

}
