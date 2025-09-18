package by.mrrockka.domain

import java.math.BigDecimal
import java.util.*

data class MoneyTransfer(
        val personId: UUID,
        val gameId: UUID,
        val amount: BigDecimal,
        val type: TransferType,
)

enum class TransferType {
    DEBIT, CREDIT, EQUAL
}
