package by.mrrockka.scenario.game

import by.mrrockka.Command
import by.mrrockka.Given
import by.mrrockka.When
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
            user { players.createGame(gameType(), buyin) }
            user { prizePool(size) }
            user { winners.finalePlaces() }
            user { calculate }
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
            user { players.createGame(gameType(), buyin) }
            if (!missed.contains("prize pool")) user { prizePool(2) }
            if (!missed.contains("finale places")) user { winners.finalePlaces() }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when $missed is missed")
    }


    @ParameterizedTest
    @ValueSource(
            strings = [
                """/prize_pool 
                |1. 90%, 2. 50%
                """,
                """/prize_pool
                |1. 90%
                """,
                """/prize_pool 
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
            user { players.createGame(gameType(), buyin) }
            user { prizePool.trimMargin() }
            user { winner.finalePlaces() }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when prize pool sum is not equal 100 percent. $fileName")
    }

    @Test
    fun `change finale places and prize pool multiple times`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            user { player.createGame(gameType(), buyin) }
            user { "nickname3".entry() }
            user { prizePool(1) }
            user { "nickname1".entry() }
            user { player.finalePlaces() }
            user { "nickname2".entry() }
            user { prizePool(2) }
            user { listOf("me", "nickname2").finalePlaces() }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `prize pool conversation command should store`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            user { player.createGame(gameType(), buyin) }
            user { "nickname3".entry() }
            val toDelete = mutableListOf<Command.UserMessage>()
            user { "/pp" }
            toDelete += user { "3" }
            toDelete += user { "50" }
            toDelete += user { "30" }
            toDelete += user { "20" }
            toDelete.deleted()
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}