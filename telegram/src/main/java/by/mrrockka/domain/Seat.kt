package by.mrrockka.domain

import kotlinx.serialization.Serializable

@Serializable
data class Seat(
        val num: Int,
        val nickname: String,
)