package by.mrrockka.scenario.game

import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Given
import by.mrrockka.scenario.Then
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.calculateResponse
import by.mrrockka.scenario.UserCommand.Companion.prizePoolRequest
import by.mrrockka.scenario.When
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class TournamentGameScenario : AbstractScenarioTest() {

    @Test
    fun `given tournament game when prize pool and finale places set up then should be able to calculate`() {
        val buyin = 10
        val type = GameType.TOURNAMENT
        val players = listOf(
                "@nickname1",
                "@nickname2"
        )

        Given {
            givenGameCreated(type, buyin, players)
            command { message(prizePoolRequest(players.size - 1)) }
            command { message(calculate) }
        } When {
            updatesReceived()
        } Then {
            expect { text<SendMessage>(calculateResponse(type, "@nickname1", 10, 10)) }
            expect { text<SendMessage>(calculateResponse(type, "@nickname1", 10, 10)) }
        }
    }


}
