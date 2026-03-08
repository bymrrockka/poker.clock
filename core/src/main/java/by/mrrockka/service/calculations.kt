package by.mrrockka.service

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.GameSummary
import by.mrrockka.domain.PrizeGameSummary
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.gameSummary
import by.mrrockka.domain.totalEntries
import by.mrrockka.domain.totalWithdrawals
import by.mrrockka.feature.ServiceFeeFeature
import java.math.BigDecimal
import java.math.RoundingMode

fun Game.toTournamentSummary(serviceFeeFeature: ServiceFeeFeature = ServiceFeeFeature()): List<PrizeGameSummary> = toSummary(serviceFeeFeature)
        .filter { it is PrizeGameSummary }
        .map { it as PrizeGameSummary }

fun Game.toSummary(serviceFee: ServiceFeeFeature = ServiceFeeFeature()): List<GameSummary> {
    return when (this) {
        is TournamentGame -> gameSummary(serviceFee)
        is BountyTournamentGame -> gameSummary(serviceFee)
        is CashGame -> gameSummary(serviceFee)
        else -> error("Unknown game type")
    }
}

fun Game.moneyInGame(): BigDecimal =
        when (this) {
            is CashGame -> players.totalEntries() - players.totalWithdrawals()
            is BountyTournamentGame -> players.totalEntries() + (players.sumOf { it.entries.size }.toBigDecimal() * bounty)
            is TournamentGame -> players.totalEntries()
            else -> error("Unknown game")
        }

fun BigDecimal.defaultScale(): BigDecimal = this.setScale(0, RoundingMode.HALF_DOWN)

