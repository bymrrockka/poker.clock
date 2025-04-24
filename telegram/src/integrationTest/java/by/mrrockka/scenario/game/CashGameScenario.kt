package by.mrrockka.scenario.game

import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.UserCommand.Companion.withdrawalRequest
import by.mrrockka.scenario.UserCommand.Companion.withdrawalResponse
import by.mrrockka.scenario.When
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class CashGameScenario : AbstractScenarioTest() {
    @Test
    fun `given cash game when all money withdrawn then should be able to calculate`() {
        val buyin = 10
        val type = GameType.CASH
        val players = listOf("@nickname1")

        givenGameCreated(type, buyin, players)
        Given {
            command { message(withdrawalRequest("nickname1", buyin)) }
//            command { message(UserCommand.gameStats) }
//            command { message(UserCommand.calculate) }
        } When {
            updatesReceived()
        } Then {
            expect { text<SendMessage>(withdrawalResponse("nickname1", buyin)) }
//            expect { text<SendMessage>(gameStatsResponse(GameType.CASH, players.size, 10)) }
//            expect { text<SendMessage>(calculateResponse(type, "@nickname1", 10, 10)) }
        }
    }

}
