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

class TournamentGameScenario : PrizeGameScenario() {
    override fun gameType(): GameType = GameType.TOURNAMENT

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `create game with players and some reentries`(withAlias: Boolean) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )
        val winners = players.take(2);

        Given {
            val game = user { players.createGame(GameType.TOURNAMENT, buyin, withAlias) }
            bot { "Game created" }
            game.pinned()
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            val prizePool = user { prizePool(2) }
            bot { "Prize pool stored" }
            prizePool.pinned()
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            val finalePlaces = user { winners.finalePlaces() }
            bot { "Finale places stored" }
            finalePlaces.pinned()
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { calculate }.pinned()
            bot { "Calculated payouts" }
            unpinned(game, prizePool, finalePlaces)
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("create game with players and some reentries${if (withAlias) " with alias" else ""}")
    }

    @Test
    fun `create game with one player and later entries`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            user { player.createGame(GameType.TOURNAMENT, buyin) }
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
            user { calculate }
            bot { "Calculated payouts" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
