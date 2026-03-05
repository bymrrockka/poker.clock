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
            bot { "Game created" }
            game.pinned()
            user { "me" kicked "nickname3" }
            bot { "Entry stored" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { "me" kicked "nickname3" }
            bot { "Player kicked" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { "nickname1" kicked "nickname4" }
            bot { "Player kicked" }
            user { "nickname1" kicked "nickname5" }
            bot { "Player kicked" }
            val prizePool = user { prizePool(2) }
            bot { "Prize pool stored" }
            prizePool.pinned()
            user { "nickname3" kicked "nickname1" }
            bot { "Player kicked" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname3" kicked "nickname1" }
            bot { "Player kicked" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            val finalePlaces = user { winners.finalePlaces() }
            bot { "Finale places stored" }
            finalePlaces.pinned()
            user { "nickname2" kicked "nickname1" }
            bot { "Player kicked" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname3" }
            bot { "Player kicked" }
            user { "nickname2" kicked "me" }
            bot { "Player kicked" }
            user { "nickname1" kicked "nickname2" }
            bot { "Player kicked" }
            user { calculate }.pinned()
            bot { "Calculated payouts" }
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
            bot { "Game created" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { "me" kicked "nickname3" }
            bot { "Player kicked" }
            user { "nickname3".entry() }
            bot { "Entry stored" }
            user { prizePool(1) }
            bot { "Prize pool stored" }
            user { "nickname2".entry() }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname3" }
            bot { "Player kicked" }
            user { "nickname1".entry() }
            bot { "Entry stored" }
            user { "nickname2" kicked "nickname1" }
            bot { "Player kicked" }
            user { player.finalePlaces() }
            bot { "Finale places stored" }
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
            user { player.createGame(GameType.BOUNTY, buyin) }
            bot { "Game created" }
            user { "me" kicked "nickname3" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Exception" }
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
            bot { "Game created" }
            user { "me" kicked "me" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Exception" }
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
            bot { "Game created" }
            user { "nickname" kicked "me" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Exception" }
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
            bot { "Game created" }
            user { "me" kicked "nickname" }
            bot { "Player kicked" }
            user { "me" kicked "nickname" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Exception" }
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
            bot { "Game created" }
            user { "nickname" kicked "me" }
            bot { "Player kicked" }
            user { "me" kicked "nickname" }
            bot { "Player kicked" }
            user { calculate }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }
}
