package by.mrrockka.response.builder

import by.mrrockka.domain.*
import java.math.BigDecimal.ZERO

class CalculationResponseBuilder(
        private val game: Game,
        private val payouts: List<Payout>,
) : ResponseBuilder {

    override fun response(): String {
        return when (game) {
            is CashGame -> payouts.joinToString(separator = "\n") {
                val player = it.creditor as CashPlayer
                val entries = player.entries.total()
                val withdrawals = player.withdrawals.total()
                """
                -----------------------------
                Payout to: @${player.person.nickname}
                    Entries: ${entries}
                    Withdrawals: ${withdrawals}
                    Total: ${it.total} 
                From:
                    ${it.debtors.joinToString { "@${it.player.person.nickname} -> ${it.debt}" }}
                """.trimIndent()
            }

            is TournamentGame -> {
                val summary = game.toSummary()
                val finalePlacesResponse = """
                -----------------------------
                Finale summary:
                    ${summary.joinToString("\n") { "${it.position}. @${it.player.person.nickname} won ${it.amount}" }}
                    Total: ${game.players.totalEntries()} (${game.players.flatMap { it.entries }.size} entries * ${game.buyIn} buy in)
                """.trimIndent()

                val payoutsResponse = payouts.joinToString(separator = "\n") {
                    val player = it.creditor as TournamentPlayer
                    val entries = player.entries.total()
                    val prize = summary.find { summary -> summary.player.person == it.creditor.person }
                    """
                    -----------------------------
                    Payout to: @${player.person.nickname}
                        Entries: ${player.entries.size}
                        Total: ${it.total} (won ${prize} - entries ${entries})
                    From:
                        ${it.debtors.joinToString { "@${it.player.person.nickname} -> ${it.debt}" }}
                    """.trimIndent()
                }

                return finalePlacesResponse + payoutsResponse
            }

            is BountyTournamentGame -> {
                val summary = game.toSummary()
                val finalePlacesResponse = """
                -----------------------------
                Finale summary:
                    ${summary.joinToString("\n") { "${it.position}. @${it.player.person.nickname} won ${it.amount}" }}
                    Total: ${game.players.totalEntries()} (${game.players.flatMap { it.entries }.size} entries * ${game.buyIn} buy in)
                """.trimIndent()

                val payoutsResponse = payouts.joinToString(separator = "\n") {
                    val player = it.creditor as BountyPlayer
                    val entries = player.entries.total()
                    val prize = summary.find { summary -> summary.player.person == it.creditor.person }
                    val (taken, given) = player.takenToGiven()
                    val bountiesTotal = taken.total() - given.total()
                    """
                    -----------------------------
                    Payout to: @${player.person.nickname}
                        Entries: ${player.entries.size}
                        Bounties: ${bountiesTotal} (taken ${taken.size} - given ${given.total()} ) 
                        Total: ${it.total} (won ${prize} - entries ${entries} ${if (bountiesTotal < ZERO) "-" else "+"} bounties ${bountiesTotal})
                    From:
                        ${it.debtors.joinToString { "@${it.player.person.nickname} -> ${it.debt}" }}
                    """.trimIndent()
                }

                return finalePlacesResponse + payoutsResponse
            }

            else -> error("Unknown game type")
        }
    }
}