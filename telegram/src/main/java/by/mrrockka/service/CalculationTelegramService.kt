package by.mrrockka.service

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Payout
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.total
import org.springframework.stereotype.Service

@Service
class CalculationTelegramService(
        val calculationService: CalculationService,
        val gameService: GameTelegramService,
) {

    fun calculate(messageMetadata: MessageMetadata): List<Payout> {
        val telegramGame = gameService.findGame(messageMetadata)
        telegramGame.game.validateGame()

        val payouts = calculationService.calculateAndSave(telegramGame.game)
        check(payouts.isNotEmpty()) { "Payouts are not calculated." }

        return payouts
    }

    private fun Game.validateGame() {
        val players = this.players
        check(players.isNotEmpty()) { "There should be players to calculate game" }

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
                check(!this.finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!this.prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }
            }

            is BountyTournamentGame -> {
                check(!this.finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!this.prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }

            }

            else -> error("Not a tournament")
        }
    }

    private fun BountyTournamentGame.validateBounty() {
        val bountiesCount = this.players.sumOf { player -> player.bounties.count { bounty -> bounty.to == player.person.id } } + 1
        val entriesCount = this.players.flatMap { it.entries }.size
        check(entriesCount == bountiesCount) { "Bounties and entries size are not equal. Deviation is ${entriesCount - bountiesCount}" }
    }

    private fun CashGame.validateCash() {
        val totalEntries = this.players.flatMap { it.entries }.total()
        val totalWithdrawals = this.players.flatMap { it.withdrawals }.total()
        check(totalEntries == totalWithdrawals) { "Entries and withdrawal amounts are not equal. Deviation is ${totalEntries - totalWithdrawals}" }
    }
}