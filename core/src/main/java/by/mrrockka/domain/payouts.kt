package by.mrrockka.domain

import java.math.BigDecimal

data class Payout(
        val creditor: Person,
        val amount: BigDecimal,
        val debtors: List<Debtor>,
        val fee: BigDecimal = BigDecimal.ZERO,
) {
    val total: BigDecimal = amount - fee
}

data class Debtor(val person: Person, val debt: BigDecimal)
