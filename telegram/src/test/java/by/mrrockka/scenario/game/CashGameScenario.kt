package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.entry
import by.mrrockka.scenario.Commands.Companion.prizePool
import by.mrrockka.scenario.Commands.Companion.withdrawal
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal


class CashGameScenario : GameScenario() {
    override fun gameType(): GameType = GameType.CASH

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should calculate when all money were withdraw`(withAlias: Boolean) {
        val buyin = BigDecimal(10)
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "me",
        )

        Given {
            val game = user { players.createGame(GameType.CASH, buyin, withAlias) }
            bot { "Game created" }
            game.pinned()
            user { "nickname1".withdrawal(20) }
            bot { "Withdraw" }
            user { "nickname2".withdrawal(30) }
            bot { "Withdraw" }
            user { "nickname4".entry(20) }
            bot { "Entry stored" }
            user { "nickname3".withdrawal(30) }
            bot { "Withdraw" }
            val calculate = user { calculate }
            bot { "Calculated payouts" }
            calculate.pinned()
            game.unpinned()
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should calculate when all money were withdraw${if (withAlias) " with alias" else ""}")
    }

    @Test
    fun `should create game with one player and calculate when other entries`(approver: Approver) {
        val buyin = BigDecimal(10)

        Given {
            user { "nickname1".createGame(GameType.CASH, buyin) }
            bot { "Game created" }
            user { "nickname2".entry() }
            bot { "Entry stored" }
            user { "nickname1".withdrawal(20) }
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
        val players = listOf("nickname1", "nickname2")

        Given {
            user { players.createGame(GameType.CASH, buyin) }
            bot { "Game created" }
            user { calculate }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when withdrawal is more then money left in game`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            user { players.createGame(GameType.CASH, buyin) }
            bot { "Game created" }
            user { "nickname1".withdrawal(40) }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

    @Test
    fun `fail when prize pool added`(approver: Approver) {
        val buyin = BigDecimal(10)
        val players = listOf("nickname1", "nickname2")

        Given {
            user { players.createGame(GameType.CASH, buyin) }
            bot { "Game created" }
            user { prizePool(1) }
            bot { "Exception" }
        } When {
            updatesReceived()
        } ThenApproveWith approver
    }

}
