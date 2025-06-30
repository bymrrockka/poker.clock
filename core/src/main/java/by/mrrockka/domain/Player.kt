package by.mrrockka.domain

import java.math.BigDecimal

interface Player {
    val person: Person
    val entries: List<BigDecimal>
}

data class TournamentPlayer(
        override val person: Person,
        override val entries: List<BigDecimal>,
) : Player

data class CashPlayer(
        override val person: Person,
        override val entries: List<BigDecimal>,
        val withdrawals: List<BigDecimal> = emptyList()
) : Player

data class BountyPlayer(
        override val person: Person,
        override val entries: List<BigDecimal>,
        val bounties: List<Bounty> = emptyList(),
) : Player

fun List<BigDecimal>.total(): BigDecimal {
    return this.fold(BigDecimal.ZERO, BigDecimal::add)
}

fun List<Player>.totalEntries(): BigDecimal = flatMap { it.entries }.total()

fun Player.total(): BigDecimal = let {
    when (val player = this) {
        is CashPlayer -> player.withdrawals.total() - player.entries.total()
        is TournamentPlayer -> -player.entries.total()
        is BountyPlayer -> {
            val (taken, given) = player.bounties.partition { it.to == player.person }
            taken.total() - given.total() - player.entries.total()
        }

        else -> error("Unknown player type")
    }
}
