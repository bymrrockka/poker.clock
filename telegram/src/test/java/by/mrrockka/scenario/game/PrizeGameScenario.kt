package by.mrrockka.scenario.game

import by.mrrockka.Command
import by.mrrockka.Given
import by.mrrockka.GivenSpecification
import by.mrrockka.When
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.Commands.Companion.calculate
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
    @ValueSource(ints = [1, 2])
    fun `should fail when prize pool is different size then finale places`(size: Int) {
        val buyin = 10.toBigDecimal()
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            prizePoolFlow {
                val prize = 100 / size
                (1..size).map { it to prize }.toMap()
            }
            finalePlacesFlow {
                (1..(size + 1)).map { it to "nickname$it" }.toMap()
            }
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
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            if (!missed.contains("prize pool")) {
                prizePoolFlow(
                        1 to 50,
                        2 to 50,
                )
            }
            if (!missed.contains("finale places")) {
                finalePlacesFlow(
                        1 to "nickname1",
                        2 to "nickname2",
                )
            }
            user { calculate }
            bot { "failure" }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when $missed is missed")
    }

    @Test
    fun `change finale places and prize pool multiple times`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user("nickname3") { entry }
            bot { "Entry stored" }
            prizePoolFlow(1 to 100)
            user("nickname1") { entry }
            bot { "Entry stored" }
            finalePlacesFlow(1 to player)
            user("nickname2") { entry }
            bot { "Entry stored" }
            prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            finalePlacesFlow(
                    1 to "me",
                    2 to "nickname2",
            )
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user to store prize pool`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user("nickname2") { entry }
            bot { "Entry stored" }
            user("nickname3") { entry }
            bot { "Entry stored" }
            prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user and validate prize pool`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user("nickname3") { entry }
            bot { "Entry stored" }
            val toDelete = mutableListOf<Command>()
            user { "/prize_pool" }
            toDelete += bot { "Pool size?" }
            toDelete += user { "3" }
            toDelete += bot { "1 Percentage" }
            toDelete += user { "10" }
            toDelete += bot { "2 Percentage" }
            toDelete += user { "20" }
            toDelete += bot { "3 Percentage" }
            toDelete += user { "30" }
            toDelete += bot { "Prize pool is not valid" }
            toDelete += bot { "1 Percentage" }
            toDelete += user { "50" }
            toDelete += bot { "2 Percentage" }
            toDelete += user { "30" }
            toDelete += bot { "3 Percentage" }
            toDelete += user { "20" }
            val summary = bot { "Prize pool is valid" }
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
            createGameFlow(buyin, listOf(player))
            user("nickname3") { entry }
            bot { "Entry stored" }
            val toDelete = mutableListOf<Command>()
            user { "/prize_pool" }
            toDelete += bot { "Pool size?" }
            toDelete += user { "3" }
            toDelete += bot { "1 Percentage" }
            toDelete += user { "50" }
            toDelete += bot { "2 Percentage" }
            toDelete += user { "30" }
            toDelete += bot { "3 Percentage" }
            toDelete += user { "cancel" }
            toDelete.deleted()
            bot { "Canceled" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user to store finale places`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("me", "nickname1", "nickname2")

        Given {
            createGameFlow(buyin, players)
            user("nickname3") { entry }
            bot { "Entry stored" }
            finalePlacesFlow(
                    1 to "nickname1",
                    2 to "nickname2",
                    3 to "me",
            )
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `interact with user to create finale places but cancel`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            user("nickname3") { entry }
            bot { "Entry stored" }
            val toDelete = mutableListOf<Command>()
            user { "/finale_places" }
            toDelete += bot { "Pool size?" }
            toDelete += user { "3" }
            toDelete += bot { "1 place" }
            toDelete += user { "@nickname1" }
            toDelete += bot { "2 place" }
            toDelete += user { "@nickname2" }
            toDelete += bot { "3 place" }
            toDelete += user { "cancel" }
            toDelete.deleted()
            bot { "Canceled" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }


    protected fun GivenSpecification.finalePlacesFlow(vararg places: Pair<Int, String>) = finalePlacesFlow { places.toMap() }

    protected fun GivenSpecification.finalePlacesFlow(placesProvider: GivenSpecification.() -> Map<Int, String>): Command.BotMessage {
        with(placesProvider()) {
            val toDelete = mutableListOf<Command>()
            user { finalePlaces }
            toDelete += bot { "Pool size?" }
            toDelete += user { "$size" }
            forEach { (place, nickname) ->
                toDelete += bot { "$place place" }
                toDelete += user { "@$nickname" }
            }
            val summary = bot { "Finale places stored" }
            summary.pinned()
            toDelete.deleted()
            return summary
        }
    }

    protected fun GivenSpecification.prizePoolFlow(vararg places: Pair<Int, Int>) = prizePoolFlow { places.toMap() }

    protected fun GivenSpecification.prizePoolFlow(placesProvider: GivenSpecification.() -> Map<Int, Int>): Command.BotMessage {
        with(placesProvider()) {
            val toDelete = mutableListOf<Command>()
            user { prizePool }
            toDelete += bot { "Pool size?" }
            toDelete += user { "$size" }
            forEach { (place, percentage) ->
                toDelete += bot { "$place place" }
                toDelete += user { "$percentage" }
            }
            val summary = bot { "Prize pool stored" }
            summary.pinned()
            toDelete.deleted()
            return summary
        }
    }
}