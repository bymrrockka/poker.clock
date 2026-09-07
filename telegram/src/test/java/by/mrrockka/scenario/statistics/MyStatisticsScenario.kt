package by.mrrockka.scenario.statistics

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.common.Commands.Companion.calculate
import by.mrrockka.scenario.common.Commands.Companion.kicked
import by.mrrockka.scenario.common.Commands.Companion.myStats
import by.mrrockka.scenario.common.Commands.Companion.withdrawal
import by.mrrockka.scenario.common.ScenarioTest
import by.mrrockka.scenario.common.createGameFlow
import by.mrrockka.scenario.common.finalePlacesFlow
import by.mrrockka.scenario.common.prizePoolFlow
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MyStatisticsScenario : ScenarioTest() {

    @Test
    fun `statistics for person in chat`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "me", "nickname1", "nickname2",
        )

        Given {
            //first in tournament game
            createGameFlow(GameType.TOURNAMENT, buyin, players)
            prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            finalePlacesFlow(
                    1 to "me",
                    2 to "nickname1",
            )
            user { calculate }
            bot { "Calculated payouts" }
            user { myStats }
            bot { "My statistics" }

            //lose in cash game
            createGameFlow(GameType.CASH, buyin, players)
            user("nickname2") { withdrawal(30) }
            bot { "Withdraw" }
            user { calculate }
            bot { "Calculated payouts" }
            user { myStats }
            bot { "My statistics" }

            //second in bounty tournament game
            createGameFlow(GameType.BOUNTY, buyin, players)
            user { "nickname1" kicked "nickname2" }
            bot { "Player kicked" }
            user { "nickname1" kicked "me" }
            bot { "Player kicked" }
            prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            finalePlacesFlow(
                    1 to "nickname1",
                    2 to "me",
            )
            user { calculate }
            bot { "Calculated payouts" }
            user { myStats }
            bot { "My statistics" }

            createGameFlow(GameType.TOURNAMENT, buyin, players)
            prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            finalePlacesFlow(
                    1 to "nickname1",
                    2 to "nickname2",
            )
            user { calculate }
            bot { "Calculated payouts" }
            user { myStats }
            bot { "My statistics" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}