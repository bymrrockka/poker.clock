package by.mrrockka.domain

import java.math.BigDecimal

data class Bounty(val from: Person, val to: Person, val amount: BigDecimal)

fun List<Bounty>.total(): BigDecimal {
    return this.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
}

fun BountyPlayer.takenToGiven(): Pair<List<Bounty>, List<Bounty>> = bounties.partition { it.to == this.person }