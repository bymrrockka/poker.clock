package by.mrrockka.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class PrizeSummary(val position: Int, val player: Player, val amount: BigDecimal)
data class FinalPlace(val position: Int, val player: Player)
data class PositionPrize(val position: Int, val percentage: BigDecimal)

fun prizeSummary(finalePlaces: List<FinalPlace>?, prizePool: List<PositionPrize>?, total: BigDecimal): List<PrizeSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    var left = total
    return prizePool.sortedBy { it.position }
            .zip(finalePlaces.sortedBy { it.position })
            .mapIndexed { index, (prize, place) ->
                if (finalePlaces.size - 1 > index) PrizeSummary(place.position, place.player, left)
                else {
                    val amount = (total * prize.percentage / BigDecimal("100")).setScale(0, RoundingMode.HALF_DOWN)
                    left -= amount
                    PrizeSummary(place.position, place.player, amount)
                }
            }
}
