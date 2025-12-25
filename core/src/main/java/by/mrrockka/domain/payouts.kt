package by.mrrockka.domain

import java.math.BigDecimal

data class Payout(
        val creditor: Person,
        val total: BigDecimal,
        val debtors: List<Debtor>,
)

data class Debtor(val person: Person, val debt: BigDecimal)
