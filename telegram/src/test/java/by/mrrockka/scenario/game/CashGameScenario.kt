package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class CashGameScenario : GameScenario() {
    override fun gameType(): GameType = GameType.CASH

    @Test
    fun `should calculate when all money were withdraw`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            val game = createGameFlow(buyin, players)
            user("nickname1") { withdrawal(20) }
            bot { "Withdraw" }
            user("nickname2") { withdrawal(30) }
            bot { "Withdraw" }
            user("nickname4") { entry(20) }
            bot { "Entry stored" }
            user("nickname3") { withdrawal(30) }
            bot { "Withdraw" }
            val calculate = user { calculate }
            bot { "Calculated payouts" }
            calculate.pinned()
            game.unpinned()
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `should create game with one player and calculate when other entries`(approver: Approver) {
        val buyin = BigDecimal(10)
        val player = "me"

        Given {
            createGameFlow(buyin, listOf(player))
            user("nickname2") { entry }
            bot { "Entry stored" }
            user { withdrawal(20) }
            bot { "Withdraw" }
            user { calculate }
            bot { "Calculated payouts" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when calculation started but there are still money in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            user { calculate }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when withdrawal is more then money left in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            user("nickname1") { withdrawal(players.size * 10 + 1) }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when prize pool added`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            user { "/prize_pool" }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when finale places added`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" } + "me"

        Given {
            createGameFlow(buyin, players)
            user { "/finale_places" }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when player not in game withdraws`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = (1..5).map { "nickname$it" }

        Given {
            createGameFlow(buyin, listOf("me"))
            user { withdrawal(10) }
            bot { "Withdrawal"}
            user { calculate }
            bot { "Calculations"}

            createGameFlow(buyin, players)
            user { withdrawal(10) }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}
