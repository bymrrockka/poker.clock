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
    /*
    * idea is to get player summaries not as amounts but as game details (buyins, bounties etc)
    * this probably not gonna work with cash game so it should be a special case for it
    *
    * on the other side is calculation as is right now but with compressed total
    * in that case it's not clear how to spread the fee for bounties and withdrawals
    *
    * For tournaments
    * I already use amount state to calculate prize amounts for players - no problems here
    *
    * For Bounty Tournaments
    * I would need to use compressed total for calculations
    * At first I require to divide it by half (scale down bounty part and subtract from total prize part)
    * Use prize and amount state to calculate prizes
    *
    * For bounties I would need
    * 1. overall size of bounties
    * 2. divide bounty total by size
    * 3. Use amount state to spread the resulted amount
    *
    * For Cash
    * It's required to calculate percentage of the overall pot for each player did withdraw
    * Then apply this percentage on amount state to calculate players payouts
    *
    * */


    fun calculate(game: Game): List<Payout> {
        val computedServiceAmounts = game.setup()
        val computedPlayerAmounts = playerSummaryService.summary(game).map { it.toComputedAmount() }
        return (computedPlayerAmounts + computedServiceAmounts).toPayouts()
    }

    //could be extended to a processing strategies
    private fun Game.setup(): List<ComputedAmount> {
        return if (serviceFeeFeature.enabled) {
            val serviceFeeAmount = serviceFeeFeature.calculate(total())
            val serviceFeeTotal = ComputedAmount(
                    transferType = TransferType.CREDIT,
                    person = serviceFeeFeature.feePerson,
                    amount = serviceFeeAmount,
            )
            listOf(serviceFeeTotal)
        } else emptyList()
    }

    private fun List<ComputedAmount>.toPayouts(): List<Payout> {
        val transfersToComputedAmounts = groupBy({ it.transferType }, { it })
        val creditors = transfersToComputedAmounts[TransferType.CREDIT]?.sortedByDescending { it.amount } ?: emptyList()
        val debtors = transfersToComputedAmounts[TransferType.DEBIT]?.sortedByDescending { it.amount } ?: emptyList()
        val equals = transfersToComputedAmounts[TransferType.EQUAL] ?: emptyList()
        validate(creditors, debtors, equals)
        return creditors.calculatePayouts(debtors) + equals.toEqualPayouts()
    }

    private fun validate(creditors: List<ComputedAmount>, debtors: List<ComputedAmount>, equals: List<ComputedAmount>) {
        check((creditors + debtors + equals).isNotEmpty()) { "There must be at least one player in a game" }
        check(creditors.total() == debtors.total()) { "Debtors and creditors totals are not equal" }

        when {
            debtors.isEmpty() && creditors.isNotEmpty() -> error("There must be at least one debtor")
            creditors.isEmpty() && debtors.isNotEmpty() -> error("There must be at least one creditor")
        }
    }

    private fun List<ComputedAmount>.toEqualPayouts(): List<Payout> = map { Payout(it.person, ZERO, emptyList()) }

    private fun List<ComputedAmount>.calculatePayouts(debtorTotals: List<ComputedAmount>): List<Payout> {
        var debtorsLeft = debtorTotals

        val payouts = map { creditor ->
            val debtors = debtorsLeft.findDebtors(creditor.amount).sortedByDescending { it.debt }
            debtorsLeft = debtorsLeft - debtors
            Payout(creditor.person, creditor.amount, debtors)
        }.let { prefilled ->
            var payouts = prefilled
            debtorsLeft.map { debtor ->
                var debt = debtor.amount
                payouts = prefilled
                        .filter { it.debtors.map { it.debt }.total() - it.total != ZERO }
                        .map { payout ->
                            val leftToPay = payout.total - payout.debtors.map { it.debt }.total()
                            if (leftToPay > ZERO) {
                                debt -= leftToPay
                                payout.copy(total = payout.total, debtors = payout.debtors + Debtor(debtor.person, leftToPay))
                            } else payout
                        }
            }
            payouts
        }

        check(total() == payouts.map { it.total }.total()) { "${debtorsLeft.size} Debtors left unprocessed" }
        return payouts
    }

    private fun List<ComputedAmount>.findDebtors(amount: BigDecimal): List<Debtor> {
        val playerTotal = find { it.amount <= amount }
        val payer = playerTotal?.let { Debtor(it.person, it.amount) }
        return when {
            payer == null -> emptyList()
            payer.debt < amount -> this.minus(playerTotal).findDebtors(amount - payer.debt) + payer
            else -> listOf(payer)
        }
    }

    private fun PlayerSummary.toComputedAmount(): ComputedAmount =
            when {
                total() < ZERO -> ComputedAmount(transferType = TransferType.DEBIT, person = person, amount = -total())
                total() > ZERO -> ComputedAmount(transferType = TransferType.CREDIT, person = person, amount = total())
                else -> ComputedAmount(transferType = TransferType.EQUAL, person = person, amount = total())
            }

    private fun List<ComputedAmount>.total() = map { it.amount }.total()
}

internal class ComputedAmount(val transferType: TransferType, val person: Person, val amount: BigDecimal)

private operator fun List<ComputedAmount>.minus(debtors: List<Debtor>): List<ComputedAmount> {
    val debtorPlayers = debtors.map { it.person }
    return this.filterNot { debtorPlayers.contains(it.person) }
}
