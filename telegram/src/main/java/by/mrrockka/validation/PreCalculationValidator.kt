package by.mrrockka.validation

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.total
import org.springframework.stereotype.Component

@Component
@Deprecated("move to service or validations file")
class PreCalculationValidator {
    fun validateGame(game: Game) {
        val players = game.players
        check(players.isNotEmpty()) { "There should be players to calculate game" }

        when (game) {
            is CashGame -> validateCash(game)
            is TournamentGame -> validateTournament(game)
            is BountyTournamentGame -> {
                validateTournament(game)
                validateBounty(game)
            }
        }
    }

    private fun validateTournament(game: Game) {
        when (game) {
            is TournamentGame -> {
                check(!game.finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!game.prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }
            }

            is BountyTournamentGame -> {
                check(!game.finalePlaces.isNullOrEmpty()) { "No finale places specified, can't calculate finale summary." }
                check(!game.prizePool.isNullOrEmpty()) { "No prize pool specified, can't calculate finale summary." }

            }

            else -> error("Not a tournament")
        }
    }

    private fun validateBounty(game: BountyTournamentGame) {
        val bountiesCount = game.players.sumOf { player -> player.bounties.count { bounty -> bounty.to == player.person.id } } + 1
        val entriesCount = game.players.flatMap { it.entries }.size
        check(entriesCount == bountiesCount) { "Bounties and entries size are not equal. Deviation is ${entriesCount - bountiesCount}" }
    }

    private fun validateCash(game: CashGame) {
        val totalEntries = game.players.flatMap { it.entries }.total()
        val totalWithdrawals = game.players.flatMap { it.withdrawals }.total()
        check(totalEntries == totalWithdrawals) { "Entries and withdrawal amounts are not equal. Deviation is ${totalEntries - totalWithdrawals}" }
    }
}