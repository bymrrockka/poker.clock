package by.mrrockka.domain

import kotlinx.serialization.Serializable

@Serializable
data class Seat(
        val num: Int,
        val nickname: String,
)

@Serializable
data class Table(val id: Int) {
    lateinit var seats: Set<Seat>

    constructor(id: Int, seats: Set<Seat>) : this(id) {
        this.seats = seats
    }

    @delegate:Transient
    val byNicknames: Map<String, Seat> by lazy { seats.associateBy { it.nickname } }

    @delegate:Transient
    val byNums: Map<Int, Seat> by lazy { seats.associateBy { it.num } }

    operator fun plus(seat: Seat): Table {
        this.seats += seat
        return this
    }
    operator fun plus(seats: Collection<Seat>): Table {
        this.seats += seats
        return this
    }

    operator fun minus(seats: Collection<Seat>): Table {
        this.seats = this.seats - seats
        return this
    }
}
