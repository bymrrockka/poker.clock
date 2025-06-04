package by.mrrockka.service.calculation

import by.mrrockka.domain.*
import by.mrrockka.domain.payout.TransferType
import by.mrrockka.domain.payout.TransferType.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Component
open class GameCalculator {

    fun calculate(game: Game): List<Payout> {
        val transferTypeToPlayer = game.associateTransferTypeWithPlayers()
        val creditors = transferTypeToPlayer[CREDIT]?.sortedByDescending { it.total } ?: emptyList()
        val debtors = transferTypeToPlayer[DEBIT]?.sortedByDescending { it.total } ?: emptyList()
        val equals = transferTypeToPlayer[EQUAL] ?: emptyList()

        validate(game, creditors, debtors, equals)

        return creditors.calculatePayouts(debtors) + equals.asEqualPayouts()
    }

    private fun validate(game: Game, creditors: List<PlayerTotal>, debtors: List<PlayerTotal>, equals: List<PlayerTotal>) {
        if (game.players.size != (creditors + debtors + equals).size) error("Players size and payout size are not equal")
        if ((creditors + debtors + equals).isEmpty()) error("There must be at least one player in a game")
        if (debtors.isEmpty() && creditors.isNotEmpty()) error("There must be at least one debtor")
    }

    private fun List<PlayerTotal>.calculatePayouts(debtorTotals: List<PlayerTotal>): List<Payout> {
        var debtorsLeft = debtorTotals

        val payouts = map { creditor ->
            val debtors = debtorsLeft.findDebtors(creditor.total).sortedByDescending { it.debt }
            check(debtors.isNotEmpty()) { error("Didn't find debtors for ${creditor.player.person.nickname}") }
            debtorsLeft = debtorsLeft - debtors
            Payout(creditor.player, creditor.total, debtors)
        }.let { prefilled ->
            var payouts = prefilled
            debtorsLeft.map { debtor ->
                var debt = debtor.total
                payouts = prefilled.filter { it.creditor.total() - it.total != ZERO }
                        .map { payout ->
                            val leftToPay = payout.creditor.total() - payout.total
                            if (leftToPay >= ZERO) {
                                debt -= leftToPay
                                payout.copy(total = payout.total + leftToPay, debtors = payout.debtors + Debtor(debtor.player, leftToPay))
                            } else payout
                        }
            }
            payouts
        }

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
                val (taken, given) = player.bounties.partition { it.to == player }
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
                val playerPrize = prizeSummaries.find { prize -> it == prize.player }?.amount ?: ZERO
                it.associateByTransferType(it.total() + playerPrize)
            }.groupBy({ it.first }, { it.second })

    private fun Game.associateTransferTypeWithPlayers(): Map<TransferType, List<PlayerTotal>> =
            when (val game = this) {
                is CashGame -> game.players associateByTransferType emptyList()
                is TournamentGame -> game.players associateByTransferType game.toSummary()
                is BountyTournamentGame -> game.players associateByTransferType game.toSummary()

                else -> error("Unknown game type")
            }

    private fun Game.toSummary(): List<PrizeSummary> {
        return when (this) {
            is TournamentGame -> prizeSummary(finalePlaces = finalePlaces, prizePool = prizePool, players.totalEntries())
//            todo: bounty game
//            todo: cash game

            else -> emptyList()
        }
    }
}

internal class PlayerTotal(val player: Player, val total: BigDecimal)

private operator fun List<PlayerTotal>.minus(debtors: List<Debtor>): List<PlayerTotal> {
    val debtorPlayers = debtors.map { it.player }
    return this.filterNot { debtorPlayers.contains(it.player) }
}