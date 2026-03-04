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
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            user { prizePool(2) }
            user { players.dropLast(1).finalePlaces() }
            user { calculate }
            user { myStats }

            //lose in cash game
            user { players.createGame(GameType.CASH, buyin) }
            user { "nickname2".withdrawal(30) }
            user { calculate }
            user { myStats }

            //second in bounty tournament game
            user { players.createGame(GameType.BOUNTY, buyin) }
            user { "nickname1" kicked "nickname2" }
            user { "nickname1" kicked "me" }
            user { prizePool(2) }
            user { players.dropLast(1).reversed().finalePlaces() }
            user { calculate }
            user { myStats }

            //lose in tournament game
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            user { prizePool(2) }
            user { players.drop(1).finalePlaces() }
            user { calculate }
            user { myStats }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}