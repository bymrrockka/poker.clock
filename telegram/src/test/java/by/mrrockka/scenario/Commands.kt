package by.mrrockka.scenario

import by.mrrockka.domain.GameType
import java.math.BigDecimal
import java.math.RoundingMode

class Commands private constructor() {
    companion object {
        val calculate = "/calculate"
        val gameStats = "/game_stats"
        val playerStats = "/player_stats"
        val myStats = "/my_stats"
        val help = "/help"
        val cashGame = "/cash_game"
        val cashGameAlias = "/cg"
        val tournamentGame = "/tournament_game"
        val tournamentGameAlias = "/tg"
        val bountyGame = "/bounty_game"
        val bountyGameAlias = "/bg"
        val withdrawal = "/withdrawal"
        val entry = "/entry"
        val bounty = "/bounty"
        val prizePool = "/prize_pool"
        val finalePlaces = "/finale_places"
        val createPoll = "/create_poll"
        val stopPoll = "/stop_poll"
        val chatPoll = "chatPoll"

        fun String.entry(amount: Int? = null): String = "${entry} @$this ${if (amount == null) "" else amount}"
        fun entry(amount: Int? = null): String = "${entry} @me ${if (amount == null) "" else amount}"
        fun help(command: String? = null): String = "$help ${command ?: ""}"

        fun List<String>.createGame(type: GameType, buyin: BigDecimal, alias: Boolean = false): String {
            return """
                ${type.toCommand(alias)}
                buyin: $buyin
                ${if (type == GameType.BOUNTY) "bounty: $buyin" else ""}
                ${this.joinToString { "@$it" }}
            """.trimIndent()
        }

        fun String.createGame(type: GameType, buyin: BigDecimal): String = listOf(this).createGame(type, buyin)

        fun createGame(type: GameType, buyin: BigDecimal): String = """
                ${type.toCommand()}
                buyin: $buyin
                ${if (type == GameType.BOUNTY) "bounty: $buyin" else ""}
        """.trimIndent()

        private fun GameType.toCommand(alias: Boolean = false): String = when (this) {
            GameType.CASH -> if (alias) cashGameAlias else cashGame
            GameType.TOURNAMENT -> if (alias) tournamentGameAlias else tournamentGame
            GameType.BOUNTY -> if (alias) bountyGameAlias else bountyGame
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

        fun List<String>.finalePlaces(): String {
            return """
            ${finalePlaces}
            ${
                this.mapIndexed { index, nickname -> index to nickname }
                        .joinToString { (index, nickname) -> "${index + 1} @${nickname}" }
            }""".trimIndent()
        }

        fun String.finalePlaces(): String = listOf(this).finalePlaces()

        fun String.withdrawal(amount: Int): String = "${withdrawal} @$this $amount"

        infix fun String.kicked(kicked: String): String = "/bounty @$this kicked @$kicked"
    }

}