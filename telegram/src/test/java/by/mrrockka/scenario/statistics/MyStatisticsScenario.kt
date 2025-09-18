package by.mrrockka.scenario.statistics

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.finalePlaces
import by.mrrockka.scenario.Commands.Companion.kicked
import by.mrrockka.scenario.Commands.Companion.myStats
import by.mrrockka.scenario.Commands.Companion.prizePool
import by.mrrockka.scenario.Commands.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MyStatisticsScenario : AbstractScenarioTest() {

    @Test
    fun `statistics for person in chat`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1", "nickname2",
        )

        Given {
            //first in tournament game
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { prizePool(2) }
            message { players.dropLast(1).finalePlaces() }
            message { calculate }
            message { myStats }

            //lose in cash game
            message { players.createGame(GameType.CASH, buyin) }
            message { "nickname2".withdrawal(30) }
            message { calculate }
            message { myStats }

            //second in bounty tournament game
            message { players.createGame(GameType.BOUNTY, buyin) }
            message { "nickname1" kicked "nickname2" }
            message { "nickname1" kicked "me" }
            message { prizePool(2) }
            message { players.dropLast(1).reversed().finalePlaces() }
            message { calculate }
            message { myStats }

            //lose in tournament game
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { prizePool(2) }
            message { players.drop(1).finalePlaces() }
            message { calculate }
            message { myStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}