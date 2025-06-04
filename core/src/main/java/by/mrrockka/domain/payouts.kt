package by.mrrockka.domain

import java.math.BigDecimal

data class Payout(
        val creditor: Player,
        val total: BigDecimal,
        val debtors: List<Debtor>
)

data class Debtor(val player: Player, val debt: BigDecimal)
