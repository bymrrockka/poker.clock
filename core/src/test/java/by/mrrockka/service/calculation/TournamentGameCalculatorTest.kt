package by.mrrockka.service.calculation

import by.mrrockka.ServiceFeeFeatureTest
import by.mrrockka.builder.plus
import by.mrrockka.builder.tournamentGame
import by.mrrockka.builder.tournamentPlayer
import by.mrrockka.builder.tournamentPlayers
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Game
import by.mrrockka.domain.PositionPrize
import by.mrrockka.extension.textApprover
import by.mrrockka.feature.ServiceFeeFeature
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal

class TournamentGameCalculatorTest : ServiceFeeFeatureTest() {
    @ParameterizedTest
    @CsvSource("2", "4", "10", "20", "100")
    fun `given equal entries and one prize place should calculate`(size: Int) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayers(size) {
            buyin(buyin)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(PositionPrize(1, BigDecimal("100.0")))
            finalePlaces(FinalPlace(1, players[0].person))
        }

        game.calculateAndAssert(textApprover("given equal entries and one prize place should calculate.size $size"))
    }

    @Test
    fun `given some reentries and one prize place should calculate`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        } + tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(PositionPrize(1, BigDecimal("100.0")))
            finalePlaces(FinalPlace(1, players[0].person))
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `given equal entries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `given some reentries and two prize positions should calculate`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayers(10) {
            buyin(buyin)
        } + tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `given winners has reentries should calculate`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        } + tournamentPlayers(10) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `given winners has reentries and prize doesn't cover debt should calculate payouts`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayer {
            buyin(buyin)
            entries(3)
        } + tournamentPlayer {
            buyin(buyin)
            entries(4)
        } + tournamentPlayers(10) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("90")),
                    PositionPrize(2, BigDecimal("10.0")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `given prize amounts has decimal points should calculate`(approver: Approver) {
        val buyin = BigDecimal("10.0")
        val players = tournamentPlayers(11) { buyin(buyin) }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("75")),
                    PositionPrize(2, BigDecimal("25")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    @Test
    fun `duplicated person in debts bug`(approver: Approver) {
        val feature = ServiceFeeFeature(
                enabled = true,
                percent = BigDecimal("5"),
                threshold = BigDecimal("2"),
                description = "Service Fee",
                url = "https://www.mrrockka.by",
        )
        changeFeeTo(feature)
        val buyin = BigDecimal("30.0")
        val players = tournamentPlayers(4) { buyin(buyin) } +
                tournamentPlayer {
                    buyin(buyin)
                    entries(2)
                } +
                tournamentPlayer {
                    buyin(buyin)
                    entries(2)
                } +
                tournamentPlayer {
                    buyin(buyin)
                    entries(3)
                } +
                tournamentPlayer {
                    buyin(buyin)
                    entries(5)
                }

        val game = tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(
                    PositionPrize(1, BigDecimal("55")),
                    PositionPrize(2, BigDecimal("30")),
                    PositionPrize(3, BigDecimal("15")),
            )
            finalePlaces(
                    FinalPlace(1, players[0].person),
                    FinalPlace(2, players[1].person),
                    FinalPlace(3, players[4].person),
            )
        }

        game.calculateAndAssert(approver)
    }

    override fun game(buyin: BigDecimal, playersSize: Int, prizeSize: Int): Game {
        val players = tournamentPlayers(playersSize) { buyin(buyin) }

        return tournamentGame {
            buyIn(buyin)
            players(players)
            prizePool(prizePool(prizeSize))
            finalePlaces(players.finalePlaces(prizeSize))
        }
    }

}
