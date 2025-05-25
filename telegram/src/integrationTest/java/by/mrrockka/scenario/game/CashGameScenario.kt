package by.mrrockka.scenario.game

import by.mrrockka.creator.PersonCreator
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.GameType
import by.mrrockka.domain.Payout
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.UserCommand
import by.mrrockka.scenario.UserCommand.Companion.calculateResponse
import by.mrrockka.scenario.UserCommand.Companion.gameStatsResponse
import by.mrrockka.scenario.UserCommand.Companion.withdrawalRequest
import by.mrrockka.scenario.UserCommand.Companion.withdrawalResponse
import by.mrrockka.scenario.When
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.math.BigDecimal


class CashGameScenario : AbstractScenarioTest() {
    @Test
    fun `given cash game when all money withdrawn then should be able to calculate`() {
        val buyin = BigDecimal(10)
        val type = GameType.CASH
        val players = listOf("nickname1", "nickname2")

        val payout = Payout(
                player = CashPlayer(
                        person = PersonCreator.domain("nickname1"),
                        entries = listOf(buyin),
                        withdrawals = listOf(BigDecimal(20)),
                ),
                debtors = listOf(Debtor(
                        player = CashPlayer(
                                person = PersonCreator.domain("nickname2"),
                                entries = listOf(buyin)
                        ),
                        amount = buyin,
                )),
                total = buyin
        )


        val chatId = givenGameCreatedWithChatId(type, buyin, players)
        Given {
            command { message(withdrawalRequest("nickname1", 20)) }
            command { message(UserCommand.gameStats) }
            command { message(UserCommand.calculate) }
        } When {
            updatesReceived(chatId)
        } Then {
            expect { text<SendMessage>(withdrawalResponse("nickname1", 20)) }
            expect { text<SendMessage>(gameStatsResponse(GameType.CASH, players.size, buyin = buyin, withdrawal = 20)) }
            expect { text<SendMessage>(listOf(payout).calculateResponse()) }
        }
    }

}
