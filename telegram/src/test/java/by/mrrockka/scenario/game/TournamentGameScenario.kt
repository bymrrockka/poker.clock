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
            command { players.createGame(GameType.TOURNAMENT, buyin) }
            command { "nickname3".entry() }
            command { "nickname3".entry() }
            command { prizePool(2) }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { winners.finalePlaces() }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { "nickname1".entry() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }


    @Test
    fun `create game with one player and later entries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            command { player.createGame(GameType.TOURNAMENT, buyin) }
            command { "nickname3".entry() }
            command { prizePool(1) }
            command { "nickname1".entry() }
            command { player.finalePlaces() }
            command { "nickname2".entry() }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    override fun gameType(): GameType = GameType.TOURNAMENT
}
