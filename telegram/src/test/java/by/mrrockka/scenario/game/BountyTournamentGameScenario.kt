package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.UserCommand.Companion.calculate
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.entry
import by.mrrockka.scenario.UserCommand.Companion.finalePlaces
import by.mrrockka.scenario.UserCommand.Companion.kicked
import by.mrrockka.scenario.UserCommand.Companion.prizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test

class BountyTournamentGameScenario : GameScenario() {
    override fun gameType(): GameType = GameType.BOUNTY

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
            command { players.createGame(GameType.BOUNTY, buyin) }
            command { "me" kicked "nickname3" }
            command { "nickname3".entry() }
            command { "me" kicked "nickname3" }
            command { "nickname3".entry() }
            command { "nickname1" kicked "nickname4" }
            command { "nickname1" kicked "nickname5" }
            command { prizePool(2) }
            command { "nickname3" kicked "nickname1" }
            command { "nickname1".entry() }
            command { "nickname3" kicked "nickname1" }
            command { "nickname1".entry() }
            command { winners.finalePlaces() }
            command { "nickname2" kicked "nickname1" }
            command { "nickname1".entry() }
            command { "nickname2" kicked "nickname3" }
            command { "nickname2" kicked "me" }
            command { "nickname1" kicked "nickname2" }
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
            command { player.createGame(GameType.BOUNTY, buyin) }
            command { "nickname3".entry() }
            command { "me" kicked "nickname3" }
            command { "nickname3".entry() }
            command { prizePool(1) }
            command { "nickname2".entry() }
            command { "nickname2" kicked "nickname3" }
            command { "nickname1".entry() }
            command { "nickname2" kicked "nickname1" }
            command { player.finalePlaces() }
            command { "me" kicked "nickname2" }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

    @Test
    fun `should fail when trying to kick player not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            command { player.createGame(GameType.BOUNTY, buyin) }
            command { "me" kicked "nickname3" }
            command { calculate }
        } When {
            updatesReceived()
        } ThenApprove (approver)
    }

}
