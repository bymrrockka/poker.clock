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

        fun String.entry(amount: Int? = null): String = "${entry} @$this ${if (amount == null) "" else amount}"
        fun entry(amount: Int? = null): String = "${entry} @me ${if (amount == null) "" else amount}"

        fun List<String>.createGame(type: GameType, buyin: BigDecimal): String {
            val command = when (type) {
                GameType.CASH -> cashGame
                GameType.TOURNAMENT -> tournamentGame
                GameType.BOUNTY -> bountyGame
            }

            return """
                $command
                buyin: $buyin
                ${if (type == GameType.BOUNTY) "bounty: $buyin" else ""}
                ${this.joinToString { "@$it" }}
            """.trimIndent()
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

        fun prizePool(size: Int): String {
            return """
            $prizePool
            ${calculatePrizePool(size).entries.joinToString { (index, value) -> "${index} ${value}%" }}
            """.trimIndent()
        }

        fun finalePlaces(winners: List<String>): String {
            return """
            ${finalePlaces}
            ${
                winners
                        .mapIndexed { index, nickname -> index to nickname }
                        .joinToString { (index, nickname) -> "${index + 1} @${nickname}" }
            }""".trimIndent()
        }

        fun String.withdrawal(amount: Int): String = "${withdrawal} @$this $amount"
    }

}