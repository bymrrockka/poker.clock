package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.finalePlaces
import by.mrrockka.scenario.Commands.Companion.prizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

abstract class PrizeGameScenario : GameScenario() {

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
            message { players.createGame(gameType(), buyin) }
            message { prizePool(size) }
            message { winners.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when prize pool is different size then finale places $size")
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
            message { players.createGame(gameType(), buyin) }
            if (!missed.contains("prize pool")) message { prizePool(2) }
            if (!missed.contains("finale places")) message { winners.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when $missed is missed")
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
            message { players.createGame(gameType(), buyin) }
            message { prizePool.trimMargin() }
            message { winner.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when prize pool sum is not equal 100 percent. $fileName")
    }

    @Test
    fun `change finale places and prize pool multiple times`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            message { player.createGame(GameType.TOURNAMENT, buyin) }
            message { "nickname3".entry() }
            message { prizePool(1) }
            message { "nickname1".entry() }
            message { player.finalePlaces() }
            message { "nickname2".entry() }
            message { prizePool(2) }
            message { listOf("me", "nickname2").finalePlaces() }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}