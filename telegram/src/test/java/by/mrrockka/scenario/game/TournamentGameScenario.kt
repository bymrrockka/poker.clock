package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.entry
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TournamentGameScenario : PrizeGameScenario() {
    override fun gameType(): GameType = GameType.TOURNAMENT

    @Test
    fun `create game with players and some reentries`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            val game = createGameFlow(buyin, players)
            user("nickname3") { entry }
            bot { "Entry stored" }
            user("nickname3") { entry }
            bot { "Entry stored" }
            val prizePool = prizePoolFlow(
                    1 to 50,
                    2 to 50,
            )
            user("nickname1") { entry }
            bot { "Entry stored" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            val finalePlaces = finalePlacesFlow(
                    1 to "nickname1",
                    2 to "nickname2",
            )
            user("nickname1") { entry }
            bot { "Entry stored" }
            user("nickname1") { entry }
            bot { "Entry stored" }
            user("nickname1") { entry }
            bot { "Entry stored" }
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
            user { calculate }
            bot { "Calculated payouts" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
