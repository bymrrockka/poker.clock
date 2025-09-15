package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.entry
import by.mrrockka.scenario.UserCommand.Companion.finalePlaces
import by.mrrockka.scenario.UserCommand.Companion.prizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class TournamentGameScenario : GameScenario() {
    @Test
    fun `create game with players and some reentries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
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
            message { players.createGame(GameType.TOURNAMENT, buyin) }
            message { "nickname3".entry() }
            message { "nickname3".entry() }
            message { prizePool(2) }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { winners.finalePlaces() }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { "nickname1".entry() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }


    @Test
    fun `create game with one player and later entries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
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
        } ThenApprove (approver)
    }

    override fun gameType(): GameType = GameType.TOURNAMENT
}
