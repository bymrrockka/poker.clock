package by.mrrockka.domain

import java.math.BigDecimal

data class Payout(
        val player: Player,
        val total: BigDecimal,
        val debtors: List<Debtor>
)

data class Debtor(val player: Player, val amount: BigDecimal)

