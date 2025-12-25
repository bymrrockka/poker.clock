package by.mrrockka.service

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Payout
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.toSummary
import by.mrrockka.domain.total
import by.mrrockka.repo.GameSummaryRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface CalculationTelegramService {
    fun calculate(messageMetadata: MessageMetadata): List<Payout>
}

@Service
@Transactional
open class CalculationTelegramServiceImpl(
        val calculationService: CalculationService,
        val gameService: GameTelegramService,
        val gameSummaryRepo: GameSummaryRepo,
) : CalculationTelegramService {

    override fun calculate(messageMetadata: MessageMetadata): List<Payout> {
        val game = gameService.findGame(messageMetadata)
        game.validateGame()
        val gameSummaries = game.toSummary()
        gameSummaryRepo.store(game.id, gameSummaries)
        val payouts = calculationService.calculate(game)
        check(payouts.isNotEmpty()) { "Payouts are not calculated." }

        return payouts
    }

    private fun Game.validateGame() {
        when (this) {
            is CashGame -> validateCash()
            is TournamentGame -> validateTournament()
            is BountyTournamentGame -> {
                validateTournament()
                validateBounty()
            }
        }
    }

    private fun Game.validateTournament() {
        when (this) {
            is TournamentGame -> {
                check(!finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }
                check(finalePlaces!!.size == prizePool!!.size) { "Finale places and prize pool should be same size" }
            }

            is BountyTournamentGame -> {
                check(!finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }
                check(finalePlaces!!.size == prizePool!!.size) { "Finale places and prize pool should be same size" }
            }

            else -> error("Not a tournament")
        }
    }

    private fun BountyTournamentGame.validateBounty() {
        val bountiesCount = this.players.sumOf { player -> player.bounties.count { bounty -> bounty.to == player.person } } + 1
        val entriesCount = this.players.flatMap { it.entries }.size
        check(entriesCount == bountiesCount) { "Bounties and entries size are not equal. Deviation is ${entriesCount - bountiesCount}" }
    }

    private fun CashGame.validateCash() {
        val totalEntries = this.players.flatMap { it.entries }.total()
        val totalWithdrawals = this.players.flatMap { it.withdrawals }.total()
        check(totalEntries == totalWithdrawals) { "Entries and withdrawal amounts are not equal. Deviation is ${totalEntries - totalWithdrawals}" }
    }
}