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
            bot { "Game created"}
            user { prizePool(2) }
            bot { "Prize pool stored"}
            user { players.dropLast(1).finalePlaces() }
            bot { "Finale places stored"}
            user { calculate }
            bot { "Calculated payouts"}
            user { myStats }
            bot { "My statistics"}

            //lose in cash game
            user { players.createGame(GameType.CASH, buyin) }
            bot { "Game created"}
            user("nickname2") { withdrawal(30) }
            bot { "Withdraw" }
            user { calculate }
            bot { "Calculated payouts"}
            user { myStats }
            bot { "My statistics"}

            //second in bounty tournament game
            user { players.createGame(GameType.BOUNTY, buyin) }
            bot { "Game created"}
            user { "nickname1" kicked "nickname2" }
            bot { "Player kicked"}
            user { "nickname1" kicked "me" }
            bot { "Player kicked"}
            user { prizePool(2) }
            bot { "Prize pool stored"}
            user { players.dropLast(1).reversed().finalePlaces() }
            bot { "Finale places stored"}
            user { calculate }
            bot { "Calculated payouts"}
            user { myStats }
            bot { "My statistics"}

            //lose in tournament game
            user { players.createGame(GameType.TOURNAMENT, buyin) }
            bot { "Game created"}
            user { prizePool(2) }
            bot { "Prize pool stored"}
            user { players.drop(1).finalePlaces() }
            bot { "Finale places stored"}
            user { calculate }
            bot { "Calculated payouts"}
            user { myStats }
            bot { "My statistics"}
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}