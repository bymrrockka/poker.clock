package by.mrrockka.scenario.game

import by.mrrockka.scenario.AbstractScenarioTest
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class CashGameScenario : AbstractScenarioTest() {
    private val gameStatsCommand = "/game_stats"
    private val gameStartedResponse = "Cash game started."

    @Test
    fun `given cash game when all money withdrawn then should be able to calculate`() {
        val buyin = 10
        val players = listOf("@nickname1")

        Given {
            command { message(createGameCommand(buyin, players)) }
            command { message(gameStatsCommand) }
            command { message(withdrawalCommand("nickname1", buyin)) }
            command { message(gameStatsCommand) }
            command { message(calculateCommand) }
        } When {
            commands.updateReceived()
        } Then {
            expect { text<SendMessage>(gameStartedResponse) }
            expect { text<SendMessage>(gameStatResponse(buyin, players.size)) }
            expect { text<SendMessage>(withdrawalResponse("nickname1", 10)) }
            expect { text<SendMessage>(gameStatResponse(buyin, players.size, 10)) }
            expect { text<SendMessage>(calculateResponse("@nickname1", 10, 10)) }
        }
    }

    fun createGameCommand(buyin: Int, players: List<String>): String = """
            /cash_game
            stack: 10k
            buyin: $buyin
            ${players.joinToString("\n")}
        """.trimIndent()

    fun calculateResponse(nickname: String, entries: Int, withdrawal: Int = 0): String = """
        -----------------------------
        Payout to: $nickname
            Entries: $entries
            Withdrawals: $withdrawal
            Total: ${withdrawal - entries}
    """.trimIndent()

    fun gameStatResponse(buyin: Int, playersSize: Int, withdrawalAmount: Int = 0): String = """
            Cash game statistics:
                - players entered -> $playersSize
                - total buy-in amount -> ${buyin * playersSize}
                - total withdrawal amount -> $withdrawalAmount
        """.trimIndent()

    fun withdrawalCommand(nickname: String, amount: Int): String = "/withdrawal @$nickname $amount"
    fun withdrawalResponse(nickname: String, amount: Int): String = """
        Withdrawals: 
            - @$nickname -> $amount
    """.trimIndent()
}
