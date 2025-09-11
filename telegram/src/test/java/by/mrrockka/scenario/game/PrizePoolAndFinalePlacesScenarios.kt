package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.textApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.finalePlaces
import by.mrrockka.scenario.UserCommand.Companion.prizePool
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PrizePoolAndFinalePlacesScenarios : AbstractScenarioTest() {

    @ParameterizedTest
    @ValueSource(ints = [1, 3])
    fun `should fail when prize pool is different size then finale places`(size: Int) {
        val buyin = 10.toBigDecimal()
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )
        val winners = players.dropLast(4)

        Given {
            command { players.createGame(GameType.TOURNAMENT, buyin) }
            command { prizePool(size) }
            command { winners.finalePlaces() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("should fail when prize pool is different size then finale places $size"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["finale places", "prize pool", "prize pool && finale places"])
    fun `should fail when finale places or prize pool is missed`(missed: String) {
        val buyin = 10.toBigDecimal()
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )
        val winners = players.dropLast(4)

        Given {
            command { players.createGame(GameType.TOURNAMENT, buyin) }
            if (!missed.contains("prize pool")) command { prizePool(2) }
            if (!missed.contains("finale places")) command { winners.finalePlaces() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("should fail when $missed is missed"))
    }


    @ParameterizedTest
    @ValueSource(
            strings = [
                """/pp 
                |1. 90%, 2. 50%
                """,
                """/pp 
                |1. 90%
                """,
                """/pp 
                |1. 23%
                |2. 23%
                |3. 23%
                |4. 13%
                |5. 13%
                |6. 4%
                """,
            ],
    )
    fun `should fail when prize pool sum is not equal 100 percent`(prizePool: String) {
        val buyin = 10.toBigDecimal()
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "nickname6",
                "nickname7",
                "me",
        )
        val winner = players[0]
        val fileName = prizePool
                .trimMargin()
                .lines()
                .filterNot { it.contains("/") }
                .joinToString { it.trim() }
                .trim()

        Given {
            command { players.createGame(GameType.TOURNAMENT, buyin) }
            command { prizePool.trimMargin() }
            command { winner.finalePlaces() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (textApprover("should fail when prize pool sum is not equal 100 percent. $fileName"))
    }

}