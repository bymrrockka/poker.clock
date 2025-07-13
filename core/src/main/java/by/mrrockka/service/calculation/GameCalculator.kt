package by.mrrockka.service.calculation

import by.mrrockka.domain.*
import by.mrrockka.domain.payout.TransferType
import by.mrrockka.domain.payout.TransferType.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Component
open class GameCalculator {

    //todo: consider to refactor this class to make it extendable with strategies to figure out payouts
    fun calculate(game: Game): List<Payout> {
        val transferTypeToPlayer = game.players associateByTransferType game.toSummary()
        val creditors = transferTypeToPlayer[CREDIT]?.sortedByDescending { it.total } ?: emptyList()
        val debtors = transferTypeToPlayer[DEBIT]?.sortedByDescending { it.total } ?: emptyList()
        val equals = transferTypeToPlayer[EQUAL] ?: emptyList()

        validate(game, creditors, debtors, equals)

        return creditors.calculatePayouts(debtors) + equals.asEqualPayouts()
    }

    private fun validate(game: Game, creditors: List<PlayerTotal>, debtors: List<PlayerTotal>, equals: List<PlayerTotal>) {
        check(game.players.size == (creditors + debtors + equals).size) { "Players size and payout size are not equal" }
        check((creditors + debtors + equals).isNotEmpty()) { "There must be at least one player in a game" }
        check(creditors.map { it.total }.total() - debtors.map { it.total }.total() == ZERO) { "Debtors and creditors totals are not equal" }

        when {
            debtors.isEmpty() && creditors.isNotEmpty() -> error("There must be at least one debtor")
            creditors.isEmpty() && debtors.isNotEmpty() -> error("There must be at least one creditor")
        }
    }

    private fun List<PlayerTotal>.calculatePayouts(debtorTotals: List<PlayerTotal>): List<Payout> {
        var debtorsLeft = debtorTotals

        val payouts = map { creditor ->
            val debtors = debtorsLeft.findDebtors(creditor.total).sortedByDescending { it.debt }
            debtorsLeft = debtorsLeft - debtors
            Payout(creditor.player, creditor.total, debtors)
        }.let { prefilled ->
            var payouts = prefilled
            debtorsLeft.map { debtor ->
                var debt = debtor.total
                payouts = prefilled
                        .filter { it.debtors.map { it.debt }.total() - it.total != ZERO }
                        .map { payout ->
                            val leftToPay = payout.total - payout.debtors.map { it.debt }.total()
                            if (leftToPay >= ZERO) {
                                debt -= leftToPay
                                payout.copy(total = payout.total, debtors = payout.debtors + Debtor(debtor.player, leftToPay))
                            } else payout
                        }
            }
            payouts
        }

        check(this.map { it.total }.total() - payouts.map { it.total }.total() == ZERO) { "${debtorsLeft.size} Debtors left unprocessed" }
        return payouts
    }

    private fun List<PlayerTotal>.findDebtors(total: BigDecimal): List<Debtor> {
        val playerTotal = find { it.total <= total }
        val payer = playerTotal?.let { Debtor(it.player, it.total) }
        return when {
            payer == null -> emptyList()
            payer.debt < total -> this.minus(playerTotal).findDebtors(total - payer.debt) + payer
            else -> listOf(payer)
        }
    }

    private fun List<PlayerTotal>.asEqualPayouts(): List<Payout> {
        return map {
            when (val player = it.player) {
                is CashPlayer -> Payout(player, ZERO, emptyList())
                is TournamentPlayer -> Payout(player, ZERO, emptyList())
                is BountyPlayer -> Payout(player, ZERO, emptyList())
                else -> error("Unknown player type")
            }
        }
    }

    private fun Player.total(): BigDecimal = let {
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

    private fun Player.associateByTransferType(total: BigDecimal): Pair<TransferType, PlayerTotal> =
            when {
                total < ZERO -> DEBIT to PlayerTotal(this, -total)
                total > ZERO -> CREDIT to PlayerTotal(this, total)
                else -> EQUAL to PlayerTotal(this, total)
            }

    private infix fun List<Player>.associateByTransferType(prizeSummaries: List<PrizeSummary>): Map<TransferType, List<PlayerTotal>> =
            map {
                val playerPrize = prizeSummaries.find { prize -> it.person == prize.person }?.amount ?: ZERO
                it.associateByTransferType(it.total() + playerPrize)
            }.groupBy({ it.first }, { it.second })

}

internal class PlayerTotal(val player: Player, val total: BigDecimal)

private operator fun List<PlayerTotal>.minus(debtors: List<Debtor>): List<PlayerTotal> {
    val debtorPlayers = debtors.map { it.player }
    return this.filterNot { debtorPlayers.contains(it.player) }
}