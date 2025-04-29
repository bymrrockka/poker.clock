package by.mrrockka.service.calculation

import by.mrrockka.domain.*
import by.mrrockka.domain.game.BountyGame
import by.mrrockka.domain.game.CashGame
import by.mrrockka.domain.game.Game
import by.mrrockka.domain.game.TournamentGame
import by.mrrockka.domain.payout.TransferType.DEBIT
import by.mrrockka.domain.payout.TransferType.EQUAL
import by.mrrockka.domain.summary.player.BountyPlayerSummary
import by.mrrockka.domain.summary.player.CashPlayerSummary
import by.mrrockka.domain.summary.player.PlayerSummary
import by.mrrockka.domain.summary.player.TournamentPlayerSummary
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
open class GameCalculator {

    fun calculate(game: Game): List<Payout<*>> {
        var playersSummaries = buildPlayerSummary(game);
        return playersSummaries
                .filter { it.transferType != DEBIT }
                .map {
                    calculatePayout(
                            creditorSummary = it,
                            debtorSummaries = playersSummaries.filter { it.transferType == DEBIT }.sorted())
                }
    }

    private fun calculatePayout(creditorSummary: PlayerSummary, debtorSummaries: List<PlayerSummary>): Payout<*> {
        val payout = creditorSummary.buildPayout()

        if (creditorSummary.transferType == EQUAL) {
            return payout
        }

        val debts = mutableListOf<Payer<*>>();
        var leftToPay = creditorSummary.transferAmount;

        for (debtorSummary in debtorSummaries) {
            var debtAmount = debtorSummary.transferAmount;
            var debt = debtorSummary.buildPayerBase()
            var debtComparison = debtAmount.compareTo(leftToPay);

            if (debtComparison == 0) {
                debtorSummary.subtractCalculated(debtAmount);
                debts.add(debt.copy(amount = debtAmount))
                break
            }

            if (debtComparison < 0) {
                debtorSummary.subtractCalculated(debtAmount);
                debts.add(debt.copy(amount = debtAmount))
                leftToPay = leftToPay - debtAmount
            }

            if (debtComparison > 0) {
                debtorSummary.subtractCalculated(leftToPay);
                debts.add(debt.copy(amount = debtAmount))
                break
            }
        }

        return payout.addPayers(debts)
    }

    fun buildPlayerSummary(game: Game): List<PlayerSummary> {
        return when (game) {
            is CashGame -> emptyList()
            is BountyGame -> game.entries
                    .map { BountyPlayerSummary.of(it, game.bountyList, game.finaleSummary) }
                    .sorted()

            is TournamentGame -> emptyList()
            else -> throw IllegalArgumentException("Unknown game type");
        }

    }

    fun PlayerSummary.buildPayout(): Payout<*> {
        return when (this) {
            is CashPlayerSummary -> CashPayout(this.toPlayer() as CashPlayer, emptyList())
            is BountyPlayerSummary -> BountyPayout(this.toPlayer() as BountyPlayer, emptyList())
            is TournamentPlayerSummary -> TournamentPayout(this.toPlayer(), emptyList())
        }
    }

    fun PlayerSummary.buildPayerBase(): Payer<*> {
        return this.toPayer()
    }

    private fun PlayerSummary.toPlayer(): Player {
        return when (this) {
            is CashPlayerSummary -> CashPlayer(person, personEntries.entries, personWithdrawals.withdrawals)
            is BountyPlayerSummary -> BountyPlayer(person, personEntries.entries, personBounties.bounties)
            is TournamentPlayerSummary -> TournamentPlayer(person, personEntries.entries)
        }
    }

    private fun PlayerSummary.toPayer(): Payer<*> {
        return when (this) {
            is CashPlayerSummary -> CashPayer(this.toPlayer() as CashPlayer, BigDecimal(0))
            is BountyPlayerSummary -> BountyPayer(this.toPlayer() as BountyPlayer, BigDecimal(0))
            is TournamentPlayerSummary -> TournamentPayer(this.toPlayer(), BigDecimal(0))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Payout<*>.addPayers(debts: List<Payer<*>>): Payout<*> {
        return when {
            this::class.java.isAssignableFrom(CashPayout::class.java) -> (this as CashPayout).copy(payers = (debts as List<CashPayer>))
            this::class.java.isAssignableFrom(BountyPayout::class.java) -> (this as BountyPayout).copy(payers = (debts as List<BountyPayer>))
            this::class.java.isAssignableFrom(TournamentPayout::class.java) -> (this as TournamentPayout).copy(payers = (debts as List<TournamentPayer>))
            else -> throw IllegalArgumentException("Unknown payout type")
        }
    }
}