package by.mrrockka.domain

import java.math.BigDecimal

open class Player(
        open val person: Person,
        open val entries: List<BigDecimal>
)

typealias TournamentPlayer = Player

data class CashPlayer(
        override val person: Person,
        override val entries: List<BigDecimal>,
        val withdrawals: List<BigDecimal> = emptyList()
) : Player(person, entries)

data class BountyPlayer(
        override val person: Person,
        override val entries: List<BigDecimal>,
        val bounties: List<Bounty> = emptyList(),
) : Player(person, entries)

fun List<BigDecimal>.total(): BigDecimal {
    return this.fold(BigDecimal.ZERO, BigDecimal::add)
}

