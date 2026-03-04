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

class BountyTournamentGameScenario : PrizeGameScenario() {
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
            val game = user { players.createGame(GameType.BOUNTY, buyin, withAlias) }
            game.pinned()
            user { "me" kicked "nickname3" }
            user { "nickname3".entry() }
            user { "me" kicked "nickname3" }
            user { "nickname3".entry() }
            user { "nickname1" kicked "nickname4" }
            user { "nickname1" kicked "nickname5" }
            val prizePool = user { prizePool(2) }
            prizePool.pinned()
            user { "nickname3" kicked "nickname1" }
            user { "nickname1".entry() }
            user { "nickname3" kicked "nickname1" }
            user { "nickname1".entry() }
            val finalePlaces = user { winners.finalePlaces() }
            finalePlaces.pinned()
            user { "nickname2" kicked "nickname1" }
            user { "nickname1".entry() }
            user { "nickname2" kicked "nickname3" }
            user { "nickname2" kicked "me" }
            user { "nickname1" kicked "nickname2" }
            user { calculate }.pinned()
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
            user { player.createGame(GameType.BOUNTY, buyin) }
            user { "nickname3".entry() }
            user { "me" kicked "nickname3" }
            user { "nickname3".entry() }
            user { prizePool(1) }
            user { "nickname2".entry() }
            user { "nickname2" kicked "nickname3" }
            user { "nickname1".entry() }
            user { "nickname2" kicked "nickname1" }
            user { player.finalePlaces() }
            user { "me" kicked "nickname2" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick player not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            user { player.createGame(GameType.BOUNTY, buyin) }
            user { "me" kicked "nickname3" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when trying to kick yourself`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            user { player.createGame(GameType.BOUNTY, buyin) }
            user { "me" kicked "me" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player and not in game`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val player = "me"

        Given {
            user { player.createGame(GameType.BOUNTY, buyin) }
            user { "nickname" kicked "me" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when kicking player that already kicked`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            user { players.createGame(GameType.BOUNTY, buyin) }
            user { "me" kicked "nickname" }
            user { "me" kicked "nickname" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should fail when is kicked and want to kick another player`(approver: Approver) {
        val buyin = 10.toBigDecimal()
        val players = listOf("me", "nickname")

        Given {
            user { players.createGame(GameType.BOUNTY, buyin) }
            user { "nickname" kicked "me" }
            user { "me" kicked "nickname" }
            user { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
