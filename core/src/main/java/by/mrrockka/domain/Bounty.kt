package by.mrrockka.domain

import java.math.BigDecimal
import java.util.*

data class Bounty(val from: UUID, val to: UUID, val amount: BigDecimal)

fun List<Bounty>.total(): BigDecimal {
    return this.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
}

fun BountyPlayer.takenToGiven(): Pair<List<Bounty>, List<Bounty>> = bounties.partition { it.to == this.person.id }