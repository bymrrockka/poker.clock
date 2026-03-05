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
            bot { "Game created" }
            user { prizePool(size) }
            bot { "Prize pool stored" }
            user { winners.finalePlaces() }
            bot { "Finale places stored" }
            user { calculate }
            bot { "Calculated payouts" }
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
            bot { "Game created" }
            if (!missed.contains("prize pool")) {
                user { prizePool(2) }
                bot { "Prize pool stored" }
            }
            if (!missed.contains("finale places")) {
                user { winners.finalePlaces() }
                bot { "Finale places stored" }
            }
            user { calculate }
            bot { "Calculated payouts" }
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
            bot { "Game created" }
            user { prizePool.trimMargin() }
            bot { "Prize pool stored" }
            user { winner.finalePlaces() }
            bot { "Finale places stored" }
            user { calculate }
            bot { "Calculated payouts" }
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
            bot { "Game created" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { prizePool(1) }
            bot { "Prize pool stored" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { player.finalePlaces() }
            bot { "Finale places stored" }
            user { "nickname2".entry() }
            bot { "Entry stored" }
            user { prizePool(2) }
            bot { "Prize pool stored" }
            user { listOf("me", "nickname2").finalePlaces() }
            bot { "Finale places stored" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user to store prize pool`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            user { player.createGame(gameType(), buyin) }
            bot { "Game created" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            val toDelete = mutableListOf<Command>()
            user { "/pp" }
            toDelete += bot { "Conversation descriptor" }
            toDelete += bot { "Pool size?" }
            toDelete += user { "3" }
            toDelete += bot { "1 Percentage" }
            toDelete += user { "50" }
            toDelete += bot { "2 Percentage" }
            toDelete += user { "30" }
            toDelete += bot { "3 Percentage" }
            toDelete += user { "20" }
            val summary = bot { "Prize pool stored" }
            summary.pinned()
            toDelete.deleted()
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user to create prize pool but cancel`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            user { player.createGame(gameType(), buyin) }
            bot { "Game created" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            val toDelete = mutableListOf<Command>()
            user { "/pp" }
            toDelete += bot { "Conversation descriptor" }
            toDelete += bot { "Pool size?" }
            toDelete += user { "3" }
            toDelete += bot { "1 Percentage" }
            toDelete += user { "50" }
            toDelete += bot { "2 Percentage" }
            toDelete += user { "30" }
            toDelete += bot { "3 Percentage" }
            toDelete += user { "cancel" }
            bot { "Canceled" }
            toDelete.deleted()
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}