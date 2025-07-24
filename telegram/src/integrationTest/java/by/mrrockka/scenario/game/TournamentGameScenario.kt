package by.mrrockka.scenario.game

import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.calculateResponse
import by.mrrockka.scenario.UserCommand.Companion.finalePlacesRequest
import by.mrrockka.scenario.UserCommand.Companion.finalePlacesResponse
import by.mrrockka.scenario.UserCommand.Companion.prizePoolRequest
import by.mrrockka.scenario.UserCommand.Companion.prizePoolResponse
import by.mrrockka.scenario.When
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.math.BigDecimal

class TournamentGameScenario : AbstractScenarioTest() {

    @Test
    fun `given tournament game when prize pool and finale places set up then should be able to calculate`() {
        val buyin = 10
        val type = GameType.TOURNAMENT
        val players = listOf(
                "nickname1",
                "nickname2",
        )
        val winners = players.dropLast(1);

        givenGameCreatedWithChatId(type, BigDecimal(buyin), players)
        Given {
            command { message(prizePoolRequest(1)) }
            command { message(finalePlacesRequest(winners)) }
            command { message(calculate) }
        } When {
            updatesReceived()
        } Then {
            expect { text<SendMessage>(prizePoolResponse(1)) }
            expect { text<SendMessage>(finalePlacesResponse(winners)) }
            expect { text<SendMessage>(calculateResponse(type, "@nickname1", entries = 10)) }
        }
    }


}
