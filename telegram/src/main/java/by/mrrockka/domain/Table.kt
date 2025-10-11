package by.mrrockka.domain

import kotlinx.serialization.Serializable

@Serializable
data class Seat(
        val num: Int,
        val nickname: String,
)

@Serializable
data class Table(val id: Int, val seats: Set<Seat>) {

    @delegate:Transient
    val byNicknames: Map<String, Seat> by lazy { seats.associateBy { it.nickname } }
}
