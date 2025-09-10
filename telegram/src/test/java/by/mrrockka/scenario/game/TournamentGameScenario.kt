package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.entry
import by.mrrockka.scenario.UserCommand.Companion.finalePlaces
import by.mrrockka.scenario.UserCommand.Companion.prizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class TournamentGameScenario : AbstractScenarioTest() {

    @Test
    fun `given tournament game when prize pool and finale places set up then should be able to calculate`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )
        val winners = players.dropLast(4);

        Given {
            command { players.createGame(GameType.TOURNAMENT, buyin) }
            command { "nickname3".entry() }
            command { "nickname3".entry() }
            command { prizePool(2) }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { finalePlaces(winners) }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }


}
