package by.mrrockka.domain

import java.math.BigDecimal

data class Bounty(val from: Player, val to: Player, val amount: BigDecimal)


fun List<Bounty>.total(): BigDecimal {
    return this.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)
}
