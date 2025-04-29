package by.mrrockka.domain

import java.math.BigDecimal

data class Payer<PLAYER: Player>(
        val player: PLAYER,
        val amount: BigDecimal
)

typealias TournamentPayer = Payer<Player>
typealias CashPayer = Payer<CashPlayer>
typealias BountyPayer = Payer<BountyPlayer>


