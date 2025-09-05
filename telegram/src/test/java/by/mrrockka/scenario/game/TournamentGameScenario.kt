package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createFinalePlaces
import by.mrrockka.scenario.UserCommand.Companion.createPrizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TournamentGameScenario : AbstractScenarioTest() {

    @Test
    fun `given tournament game when prize pool and finale places set up then should be able to calculate`(approver: Approver) {
        val buyin = 10
        val type = GameType.TOURNAMENT
        val players = listOf(
                "nickname1",
                "nickname2",
        )
        val winners = players.dropLast(1);

        givenGameCreatedWithChatId(type, BigDecimal(buyin), players)
        Given {
            command { message(createPrizePool(1)) }
            command { message(createFinalePlaces(winners)) }
            command { message(calculate) }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }


}
