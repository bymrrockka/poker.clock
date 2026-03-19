package by.mrrockka.service

import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.TransferType
import by.mrrockka.domain.total
import by.mrrockka.feature.ServiceFeeFeature
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Service
open class GameCalculator(
        private val serviceFeeFeature: ServiceFeeFeature,
        private val playerSummaryService: PlayerSummaryService,
) {
    fun calculate(game: Game): List<Payout> {
        val computedFee = game.serviceCalculationFee()
        val computedAmounts = playerSummaryService.summary(game).map { it.toComputedAmount() }
                .let { summaries ->
                    if (serviceFeeFeature.enabled && computedFee.transferType == TransferType.CREDIT) {
                        val (creditors, others) = summaries.partition { it.transferType == TransferType.CREDIT }
                        val compressedCreditors = ComputationDetails(
                                computedDebt = others.total(),
                                computedFee = computedFee.total,
                        ).compress(creditors.sortedByDescending { it.total })

                        compressedCreditors + others + computedFee
                    } else summaries
                }

        val payouts = computedAmounts.toPayouts()
        check(payouts.sum() == ZERO) { "Payouts and debtors are not equal. Deviation ${payouts.sum()}. Game total ${game.total()}" }
        return payouts
    }

    private fun ComputationDetails.compress(creditors: List<ComputedAmount>): List<ComputedAmount> {
        return when {
            creditors.isNotEmpty() -> {
                val creditor = creditors.first()
                val ratio = creditor.total.setScale(2) / computedDebt
                val compressedAmount = state.decreaseAndGet(computedFee * ratio)

                listOf(creditor.copy(fee = compressedAmount)) + compress(creditors - creditor)
            }

            else -> emptyList()
        }
    }

    internal data class ComputationDetails(
            val computedDebt: BigDecimal,
            val computedFee: BigDecimal,
    ) {
        val state: AmountState = AmountState(computedFee)
    }

    private fun Game.serviceCalculationFee(): ComputedAmount {
        val serviceFeeAmount = serviceFeeFeature.calculate(total())
        return if (serviceFeeFeature.enabled && serviceFeeAmount > ZERO) {
            val serviceFeeTotal = ComputedAmount(
                    transferType = TransferType.CREDIT,
                    person = serviceFeeFeature.feePerson,
                    amount = serviceFeeAmount,
            )
            serviceFeeTotal
        } else ComputedAmount(
                transferType = TransferType.EQUAL,
                person = serviceFeeFeature.feePerson,
                amount = ZERO,
        )
    }

    private fun List<ComputedAmount>.toPayouts(): List<Payout> {
        val transfersToComputedAmounts = groupBy({ it.transferType }, { it })
        val creditors = transfersToComputedAmounts[TransferType.CREDIT]?.sortedByDescending { it.total } ?: emptyList()
        val debtors = transfersToComputedAmounts[TransferType.DEBIT]?.sortedByDescending { it.total } ?: emptyList()
        val equals = transfersToComputedAmounts[TransferType.EQUAL] ?: emptyList()
        validate(creditors, debtors, equals)
        return creditors.calculatePayouts(debtors) + equals.toEqualPayouts()
    }

    private fun validate(creditors: List<ComputedAmount>, debtors: List<ComputedAmount>, equals: List<ComputedAmount>) {
        check((creditors + debtors + equals).isNotEmpty()) { "There must be at least one player in a game" }
        check(creditors.total() == debtors.total()) { "Debtors and creditors totals are not equal. Deviation ${creditors.total() - debtors.total()}" }

        when {
            debtors.isEmpty() && creditors.isNotEmpty() -> error("There must be at least one debtor")
            creditors.isEmpty() && debtors.isNotEmpty() -> error("There must be at least one creditor")
        }
    }

    private fun List<ComputedAmount>.toEqualPayouts(): List<Payout> = map { Payout(it.person, ZERO, emptyList()) }

    private fun List<ComputedAmount>.calculatePayouts(debtorTotals: List<ComputedAmount>): List<Payout> {
        var debtorsLeft = debtorTotals

        val payouts = map { creditor ->
            val debtors = debtorsLeft.findDebtors(creditor.total).sortedByDescending { it.debt }
            debtorsLeft = debtorsLeft - debtors
            Payout(creditor = creditor.person, amount = creditor.amount, debtors = debtors, fee = creditor.fee)
        }.let { prefilled ->
            var filled = prefilled
            debtorsLeft.map { debtor ->
                var debt = debtor.total
                filled = prefilled
                        .map { payout ->
                            val leftToPay = payout.total - payout.debtors.sumOf { it.debt }
                            if (leftToPay > ZERO) {
                                debt -= leftToPay
                                payout.copy(debtors = payout.debtors + Debtor(debtor.person, leftToPay))
                            } else payout
                        }
            }
            filled
        }

        return payouts
    }

    private fun List<ComputedAmount>.findDebtors(amount: BigDecimal): List<Debtor> {
        val playerTotal = find { it.total <= amount }
        val payer = playerTotal?.let { Debtor(it.person, it.total) }
        return when {
            payer == null -> emptyList()
            payer.debt < amount -> this.minus(playerTotal).findDebtors(amount - payer.debt) + payer
            else -> listOf(payer)
        }
    }

    private fun PlayerSummary.toComputedAmount(): ComputedAmount =
            when {
                total < ZERO -> ComputedAmount(transferType = TransferType.DEBIT, person = person, amount = -total)
                total > ZERO -> ComputedAmount(transferType = TransferType.CREDIT, person = person, amount = total)
                else -> ComputedAmount(transferType = TransferType.EQUAL, person = person, amount = total)
            }

    private fun List<ComputedAmount>.total() = map { it.total }.total()
    private fun List<Payout>.sum() = sumOf { it.total - it.debtors.sumOf { it.debt } }.up()
}

internal data class ComputedAmount(val transferType: TransferType, val person: Person, val amount: BigDecimal, val fee: BigDecimal = ZERO) {
    val total: BigDecimal = amount - fee
}

private operator fun List<ComputedAmount>.minus(debtors: List<Debtor>): List<ComputedAmount> {
    val debtorPlayers = debtors.map { it.person }
    return this.filterNot { debtorPlayers.contains(it.person) }
}
