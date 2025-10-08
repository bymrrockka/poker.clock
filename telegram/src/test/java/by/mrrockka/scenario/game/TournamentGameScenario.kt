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

class TournamentGameScenario : GameScenario() {

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
        val winners = players.dropLast(4);

        Given {
            val game = message { players.createGame(GameType.TOURNAMENT, buyin, withAlias) }
            game.pinned()
            message { "nickname3".entry() }
            message { "nickname3".entry() }
            val prizePool = message { prizePool(2) }
            prizePool.pinned()
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            val finalePlaces = message { winners.finalePlaces() }
            finalePlaces.pinned()
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { calculate }.pinned()
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
            message { player.createGame(GameType.TOURNAMENT, buyin) }
            message { "nickname3".entry() }
            message { prizePool(1) }
            message { "nickname1".entry() }
            message { player.finalePlaces() }
            message { "nickname2".entry() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    override fun gameType(): GameType = GameType.TOURNAMENT
}
