package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.common.Commands.Companion.calculate
import by.mrrockka.scenario.common.Commands.Companion.entry
import by.mrrockka.scenario.common.Commands.Companion.kicked
import by.mrrockka.scenario.common.finalePlacesFlow
import by.mrrockka.scenario.common.prizePoolFlow
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class BountyTournamentGameScenario : PrizeGameScenario() {
    override fun gameType(): GameType = GameType.BOUNTY

    @Test
    fun `create game with players and some reentries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            val game = createGameFlow(buyin, players)
            user { "me" kicked "nickname3" }
            bot { "Entry stored" }
            user("nickname3") { entry }
            bot { "Entry stored" }
            user { "me" kicked "nickname3" }
            bot { "Player kicked" }
            user("nickname3") { entry }
            bot { "Entry stored" }
            user { "nickname1" kicked "nickname4" }
            bot { "Player kicked" }
            user { "nickname1" kicked "nickname5" }
            bot { "Player kicked" }
            val prizePool = prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            user { "nickname3" kicked "nickname1" }
            bot { "Player kicked" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            user { "nickname3" kicked "nickname1" }
            bot { "Player kicked" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            val finalePlaces = finalePlacesFlow(
                    1 to "nickname1",
                    2 to "nickname2",
            )
            user { "nickname2" kicked "nickname1" }
            bot { "Player kicked" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname3" }
            bot { "Player kicked" }
            user { "nickname2" kicked "me" }
            bot { "Player kicked" }
            user { "nickname1" kicked "nickname2" }
            bot { "Player kicked" }
            val calculate = user { calculate }
            bot { "Calculated payouts" }
            calculate.pinned()
            unpinned(game, prizePool, finalePlaces)
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `create game with one player and later entries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user("nickname3") { entry }
            bot { "Entry stored" }
            user { "me" kicked "nickname3" }
            bot { "Player kicked" }
            user("nickname3") { entry }
            bot { "Entry stored" }
            prizePoolFlow(1 to 100)
            user("nickname2") { entry }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname3" }
            bot { "Player kicked" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname1" }
            bot { "Player kicked" }
            finalePlacesFlow(1 to player)
            user { "me" kicked "nickname2" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Calculated payouts" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick player not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user { "me" kicked "nickname3" }
            bot { "Player is not in game exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick yourself`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user { "me" kicked "me" }
            bot { "Can't kick yourself exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player and not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user { "nickname" kicked "me" }
            bot { "Player is not in game exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player that already kicked`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            createGameFlow(buyin, players)
            user { "me" kicked "nickname" }
            bot { "Player kicked" }
            user { "me" kicked "nickname" }
            bot { "Player kicked exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when is kicked and want to kick another player`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            createGameFlow(buyin, players)
            user { "nickname" kicked "me" }
            bot { "Player kicked" }
            user { "me" kicked "nickname" }
            bot { "Player kicked exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
