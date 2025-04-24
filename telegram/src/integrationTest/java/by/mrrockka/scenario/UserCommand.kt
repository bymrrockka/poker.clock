package by.mrrockka.scenario

import by.mrrockka.domain.GameType
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

        fun gameRequest(type: GameType, buyin: Int, players: List<String>): String {
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
                ${players.joinToString("\n")}
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

        fun gameStatsResponse(type: GameType, buyin: Int, playersSize: Int, withdrawalAmount: Int = 0): String {
            return when (type) {
                GameType.TOURNAMENT -> """
                Tournament game statistics:
                    - players entered -> $playersSize
                    - total buy-in amount -> ${buyin * playersSize}
                    - total withdrawal amount -> $withdrawalAmount
                """.trimIndent()

                GameType.CASH -> """
                Cash game statistics:
                    - players entered -> $playersSize
                    - total buy-in amount -> ${buyin * playersSize}
                    - total withdrawal amount -> $withdrawalAmount
                """.trimIndent()

                else -> ""
            }
        }

        fun prizePoolRequest(size: Int): String {
            var total = BigDecimal(100)
            val calculateForPlace = fun(): BigDecimal {
                if (size == 1) {
                    return BigDecimal(100)
                }
                val result = (total / BigDecimal(2)).setScale(-1, RoundingMode.HALF_UP).setScale(0)
                total -= result
                return result
            }

            val places = (1..size)
                    .map { it to calculateForPlace() }

            return """
            ${prizePool}
            ${places.joinToString { (index, value) -> "${index} ${value}%" }}
            """.trimIndent()
        }

        fun calculateResponse(type: GameType, nickname: String, entries: Int, withdrawal: Int = 0, bounty: Int = 0): String = when (type) {
            GameType.TOURNAMENT -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Total: ${withdrawal - entries}                
            """.trimIndent()

            GameType.CASH -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Withdrawals: $withdrawal
                Total: ${withdrawal - entries}
            """.trimIndent()

            GameType.BOUNTY -> """
            -----------------------------
            Payout to: $nickname
                Entries: $entries
                Bounty: $bounty
                Total: ${withdrawal - entries}                
            """.trimIndent()
        }

        fun withdrawalRequest(nickname: String, amount: Int): String = "${withdrawal} @$nickname $amount"
        fun withdrawalResponse(nickname: String, amount: Int): String = """
        Withdrawals: 
            - @$nickname -> $amount
        """.trimIndent()
    }

}