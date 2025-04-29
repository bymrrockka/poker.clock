package by.mrrockka.scenario

import by.mrrockka.domain.*
import java.math.BigDecimal
import java.math.RoundingMode

class UserCommand {
    companion object {
        val calculate = "/calculate"
        val gameStats = "/game_stats"
        val myStats = "/my_stats"
        val globalStats = "/global_stats"
        val help = "/help"
        val cashGame = "/cash_game"
        val tournamentGame = "/tournament_game"
        val bountyGame = "/bounty_game"
        val withdrawal = "/withdrawal"
        val entry = "/entry"
        val bounty = "/bounty"
        val prizePool = "/prize_pool"
        val finalePlaces = "/finale_places"
        val createPoll = "/create_poll"
        val stopPoll = "/stop_poll"

        fun gameRequest(type: GameType, players: List<String>, buyin: BigDecimal): String {
            val command = when (type) {
                GameType.CASH -> cashGame
                GameType.TOURNAMENT -> tournamentGame
                GameType.BOUNTY -> bountyGame
            }

            return """
                $command
                stack: 10k
                buyin: $buyin
                ${if (type == GameType.BOUNTY) "bounty: $buyin" else ""}
                ${players.joinToString { "@$it" }}
            """.trim()
        }

        fun gameResponse(type: GameType): String {
            val command = when (type) {
                GameType.CASH -> "Cash"
                GameType.TOURNAMENT -> "Tournament"
                GameType.BOUNTY -> "Bounty tournament"
            }

            return "$command game started."
        }

        fun gameStatsResponse(type: GameType, playersSize: Int, buyin: BigDecimal, withdrawal: Int = 0, numberOfEntries: Int = playersSize): String {
            return when (type) {
                GameType.TOURNAMENT -> """
                Tournament game statistics:
                    - players entered -> $playersSize
                    - number of entries -> $numberOfEntries
                    - total buy-in amount -> ${buyin * BigDecimal(numberOfEntries)}
                """.trimIndent()

                GameType.CASH -> """
                Cash game statistics:
                    - players entered -> $playersSize
                    - total buy-in amount -> ${buyin * BigDecimal(playersSize)}
                    - total withdrawal amount -> $withdrawal
                """.trimIndent()

                else -> ""
            }
        }

        private fun calculatePrizePool(size: Int): Map<Int, BigDecimal> {
            var total = BigDecimal(100)
            val calculatePercentageForPlace = fun(index: Int): BigDecimal {
                if (size == 1) {
                    return BigDecimal(100)
                }
                if (index == size) {
                    return total
                }
                val result = (total / BigDecimal(2)).setScale(-1, RoundingMode.HALF_UP).setScale(0)
                total -= result
                return result
            }

            return (1..size).associate { it to calculatePercentageForPlace(it) }
        }

        fun prizePoolRequest(size: Int): String {
            return """
            ${prizePool}
            ${
                calculatePrizePool(size).entries
                        .joinToString { (index, value) -> "${index} ${value}%" }
            }
            """.trimIndent()
        }

        fun prizePoolResponse(size: Int): String {
            return """
            Prize pool stored:
            ${
                calculatePrizePool(size).entries
                        .joinToString { (index, value) -> "${index}. -> ${value}%" }
            }
            """.trimIndent()
        }

        fun finalePlacesRequest(winners: List<String>): String {
            return """
            ${finalePlaces}
            ${
                winners
                        .mapIndexed { index, nickname -> index to nickname }
                        .joinToString { (index, nickname) -> "${index + 1} ${nickname}" }
            }
            """.trimIndent()
        }

        fun finalePlacesResponse(winners: List<String>): String {
            return """
            Finale places stored:
            ${
                winners
                        .mapIndexed { index, nickname -> index to nickname }
                        .joinToString { (index, nickname) -> "${index + 1}. -> ${nickname}" }
            }
            """.trimIndent()
        }

        fun calculateResponse(type: GameType, nickname: String, entries: Int, payees: Map<String, BigDecimal>? = emptyMap(), withdrawal: Int = 0, bounty: Int = 0): String = when (type) {
            GameType.TOURNAMENT -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Total: ${entries}                
            """.trimIndent()

            GameType.CASH -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Withdrawals: $withdrawal
                Total: ${withdrawal - entries} ($withdrawal - $entries)
            """.trimIndent()

            GameType.BOUNTY -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Bounty: $bounty
                Total: ${withdrawal - entries}                
            """.trimIndent()
        }

        @Suppress("UNCHECKED_CAST")
        fun List<Payout<*>>.calculateResponse(): String {
            val gameSummaryResponse = when {
                all { it::class.java.isAssignableFrom(BountyPayout::class.java) } -> {
                    val allEntries = flatMap { it.player.entries + it.payers.flatMap { it.player.entries } }
                    """
                    -----------------------------
                    Finale places:
                        ${mapIndexed { index, payout -> "${index}. @${payout.player.person.nickname} won ${payout.total()}" }.joinToString()}
                        Total: ${allEntries.total()} (${allEntries.size}entries * ${allEntries.first()}buy in)
                    """.trimIndent()
                }

                all { it::class.java.isAssignableFrom(TournamentPayout::class.java) } -> {
                    val allEntries = flatMap { it.player.entries + it.payers.flatMap { it.player.entries } }
                    """
                    -----------------------------
                    Finale places:
                        ${mapIndexed { index, payout -> "${index}. @${payout.player.person.nickname} won ${payout.total()}" }.joinToString()}
                        Total: ${allEntries.total()} (${allEntries.size}entries * ${allEntries.first()}buy in)
                    """.trimIndent()
                }

                else -> ""
            }


            val payoutsResponse = joinToString {
                when {
                    it::class.java.isAssignableFrom(CashPayout::class.java) -> {
                        @Suppress("UNCHECKED_CAST")
                        val payout = it as CashPayout
                        val entries = payout.player.entries.total()
                        val withdrawals = payout.player.withdrawals.total()
                        """
                            -----------------------------
                            Payout to: @${payout.player.person.nickname}
                                Entries: ${entries}
                                Withdrawals: ${withdrawals}
                                Total: ${withdrawals - entries} ($withdrawals - $entries)
                            From:
                                ${it.payers.joinToString { "@${it.player.person.nickname} -> ${it.amount}" }}
                            """.trimIndent()
                    }

                    it::class.java.isAssignableFrom(TournamentPayout::class.java) -> {
                        @Suppress("UNCHECKED_CAST")
                        val payout = it as TournamentPayout
                        val entries = payout.player.entries
                        gameSummaryResponse + """
                        -----------------------------
                        Payout to: @${payout.player.person.nickname}
                            Entries: ${entries}
                            Total: ${payout.total()} (- $entries)
                        From:
                            ${it.payers.joinToString { "@${it.player.person.nickname} -> ${it.amount}" }}
                        """.trimIndent()
                    }

                    else -> throw IllegalStateException("No type of payout")
                }
            }
            return payoutsResponse
        }

        fun withdrawalRequest(nickname: String, amount: Int): String = "${withdrawal} @$nickname $amount"
        fun withdrawalResponse(nickname: String, amount: Int): String = """
        Withdrawals: 
            - @$nickname -> $amount
        """.trimIndent()
    }

}