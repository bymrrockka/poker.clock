package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.finalePlaces
import by.mrrockka.scenario.Commands.Companion.kicked
import by.mrrockka.scenario.Commands.Companion.prizePool
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BountyTournamentGameScenario : GameScenario() {
    override fun gameType(): GameType = GameType.BOUNTY

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `create game with players and some reentries`(withAlias: Boolean) {
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
            val game = message { players.createGame(GameType.BOUNTY, buyin, withAlias) }
            game.pinned()
            message { "me" kicked "nickname3" }
            message { "nickname3".entry() }
            message { "me" kicked "nickname3" }
            message { "nickname3".entry() }
            message { "nickname1" kicked "nickname4" }
            message { "nickname1" kicked "nickname5" }
            val prizePool = message { prizePool(2) }
            prizePool.pinned()
            message { "nickname3" kicked "nickname1" }
            message { "nickname1".entry() }
            message { "nickname3" kicked "nickname1" }
            message { "nickname1".entry() }
            val finalePlaces = message { winners.finalePlaces() }
            finalePlaces.pinned()
            message { "nickname2" kicked "nickname1" }
            message { "nickname1".entry() }
            message { "nickname2" kicked "nickname3" }
            message { "nickname2" kicked "me" }
            message { "nickname1" kicked "nickname2" }
            message { calculate }.pinned()
            unpinned(game, prizePool, finalePlaces)
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("create game with players and some reentries${if (withAlias) " with alias" else ""}")
    }


    @Test
    fun `create game with one player and later entries`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            message { player.createGame(GameType.BOUNTY, buyin) }
            message { "nickname3".entry() }
            message { "me" kicked "nickname3" }
            message { "nickname3".entry() }
            message { prizePool(1) }
            message { "nickname2".entry() }
            message { "nickname2" kicked "nickname3" }
            message { "nickname1".entry() }
            message { "nickname2" kicked "nickname1" }
            message { player.finalePlaces() }
            message { "me" kicked "nickname2" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick player not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            message { player.createGame(GameType.BOUNTY, buyin) }
            message { "me" kicked "nickname3" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick yourself`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            message { player.createGame(GameType.BOUNTY, buyin) }
            message { "me" kicked "me" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player and not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            message { player.createGame(GameType.BOUNTY, buyin) }
            message { "nickname" kicked "me" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player that already kicked`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            message { players.createGame(GameType.BOUNTY, buyin) }
            message { "me" kicked "nickname" }
            message { "me" kicked "nickname" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when is kicked and want to kick another player`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            message { players.createGame(GameType.BOUNTY, buyin) }
            message { "nickname" kicked "me" }
            message { "me" kicked "nickname" }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
