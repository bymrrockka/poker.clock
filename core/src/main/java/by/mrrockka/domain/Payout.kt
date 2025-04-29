package by.mrrockka.domain

import java.math.BigDecimal

data class Payout<PLAYER : Player>(
        val player: PLAYER,
        val payers: List<Payer<PLAYER>>,
)

typealias TournamentPayout = Payout<TournamentPlayer>
typealias CashPayout = Payout<CashPlayer>
typealias BountyPayout = Payout<BountyPlayer>

fun Payout<*>.total(): BigDecimal {
    return this.payers.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
}
