package by.mrrockka.domain

import java.math.BigDecimal
import java.math.RoundingMode

interface GameSummary {
    val person: Person
    val amount: BigDecimal
}

data class TournamentSummary(val position: Int, override val person: Person, override val amount: BigDecimal) : GameSummary
data class CashSummary(override val person: Person, override val amount: BigDecimal) : GameSummary

data class FinalPlace(val position: Int, val person: Person)
data class PositionPrize(val position: Int, val percentage: BigDecimal)

fun prizeSummary(finalePlaces: List<FinalPlace>?, prizePool: List<PositionPrize>?, entriesTotal: BigDecimal): List<TournamentSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    var left = entriesTotal
    return prizePool.sortedBy { it.position }
            .zip(finalePlaces.sortedBy { it.position })
            .mapIndexed { index, (prize, place) ->
                if (finalePlaces.size - 1 <= index) TournamentSummary(place.position, place.person, left)
                else {
                    val amount = (entriesTotal * prize.percentage / BigDecimal("100")).setScale(0, RoundingMode.HALF_DOWN)
                    left -= amount
                    TournamentSummary(place.position, place.person, amount)
                }
            }
}

fun cashSummary(players: List<CashPlayer>): List<CashSummary> = players.map {
    CashSummary(
            person = it.person,
            amount = it.withdrawals.total(),
    )
}
