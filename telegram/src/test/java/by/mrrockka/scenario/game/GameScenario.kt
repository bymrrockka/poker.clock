package by.mrrockka.scenario.game

import by.mrrockka.Given
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.When
import by.mrrockka.domain.GameType
import by.mrrockka.extension.mdApprover
import by.mrrockka.scenario.AbstractScenarioTest
import by.mrrockka.scenario.Commands.Companion.calculate
import by.mrrockka.scenario.Commands.Companion.createGame
import by.mrrockka.scenario.Commands.Companion.finalePlaces
import by.mrrockka.scenario.Commands.Companion.prizePool
import by.mrrockka.service.GameSeatsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired

abstract class GameScenario : AbstractScenarioTest() {
    abstract fun gameType(): GameType

    @Autowired
    lateinit var gameSeatsService: GameSeatsService

    @BeforeEach
    fun before() {
        gameSeatsService.seed(telegramRandoms.seed.hashCode().toLong())
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 3])
    fun `should fail when prize pool is different size then finale places`(size: Int) {
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
            message { players.createGame(gameType(), buyin) }
            message { prizePool(size) }
            message { winners.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when prize pool is different size then finale places $size")
    }

    @ParameterizedTest
    @ValueSource(strings = ["finale places", "prize pool", "prize pool && finale places"])
    fun `should fail when finale places or prize pool is missed`(missed: String) {
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
            message { players.createGame(gameType(), buyin) }
            if (!missed.contains("prize pool")) message { prizePool(2) }
            if (!missed.contains("finale places")) message { winners.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when $missed is missed")
    }


    @ParameterizedTest
    @ValueSource(
            strings = [
                """/pp 
                |1. 90%, 2. 50%
                """,
                """/pp 
                |1. 90%
                """,
                """/pp 
                |1. 23%
                |2. 23%
                |3. 23%
                |4. 13%
                |5. 13%
                |6. 4%
                """,
            ],
    )
    fun `should fail when prize pool sum is not equal 100 percent`(prizePool: String) {
        val buyin = 10.toBigDecimal()
        val players = listOf(
                "nickname1",
                "nickname2",
                "nickname3",
                "nickname4",
                "nickname5",
                "nickname6",
                "nickname7",
                "me",
        )
        val winner = players[0]
        val fileName = prizePool
                .trimMargin()
                .lines()
                .filterNot { it.contains("/") }
                .joinToString { it.trim() }
                .trim()

        Given {
            message { players.createGame(gameType(), buyin) }
            message { prizePool.trimMargin() }
            message { winner.finalePlaces() }
            message { calculate }
        } When {
            updatesReceived()
        } ThenApproveWith mdApprover("should fail when prize pool sum is not equal 100 percent. $fileName")
    }

}