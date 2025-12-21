package by.mrrockka.domain

import java.math.BigDecimal
import java.math.RoundingMode

interface GameSummary {
    val person: Person
    val entries: BigDecimal

    fun total(): BigDecimal
}

interface PrizeGameSummary : GameSummary {
    val position: Int?
    val prize: BigDecimal
}

data class TournamentSummary(
        override val person: Person,
        override val entries: BigDecimal,
        override val position: Int? = null,
        override val prize: BigDecimal,
) : PrizeGameSummary {
    override fun total(): BigDecimal = prize - entries
}

data class BountySummary(
        override val person: Person,
        override val entries: BigDecimal,
        override val prize: BigDecimal,
        override val position: Int? = null,
        val bounties: BigDecimal,
) : PrizeGameSummary {
    override fun total(): BigDecimal = bounties + prize - entries
}

data class CashSummary(
        override val person: Person,
        override val entries: BigDecimal,
        val withdrawals: BigDecimal,
) : GameSummary {
    override fun total(): BigDecimal = withdrawals - entries
}

data class FinalPlace(val position: Int, val person: Person)
data class PositionPrize(val position: Int, val percentage: BigDecimal)
internal data class FinalPrizeSummary(val position: Int, val amount: BigDecimal, val person: Person)

fun TournamentGame.gameSummary(): List<TournamentSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizePool!!.prizeSummary(finalePlaces!!, players.flatMap { it.entries }.total())

    return players.map { player ->
        val prize = prizeSummary[player.person]
        TournamentSummary(
                person = player.person,
                entries = player.entries.total(),
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
        )
    }
}

fun BountyTournamentGame.gameSummary(): List<BountySummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizePool!!.prizeSummary(finalePlaces!!, players.flatMap { it.entries }.total())

    return players.map { player ->
        val prize = prizeSummary[player.person]
        val (taken, given) = player.takenToGiven()
        BountySummary(
                person = player.person,
                entries = player.entries.total(),
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
                bounties = taken.total() - given.total(),
        )
    }
}

fun CashGame.gameSummary(): List<CashSummary> = players.map {
    CashSummary(
            person = it.person,
            entries = it.entries.total(),
            withdrawals = it.withdrawals.total(),
    )
}

private fun List<PositionPrize>.prizeSummary(finalePlaces: List<FinalPlace>, entries: BigDecimal): Map<Person, FinalPrizeSummary> {
    var left = entries
    return this.sortedBy { it.position }
            .zip(finalePlaces.sortedBy { it.position })
            .mapIndexed { index, (prize, place) ->
                if (finalePlaces.size - 1 <= index) FinalPrizeSummary(
                        position = place.position,
                        person = place.person,
                        amount = left,
                )
                else {
                    val amount = (entries * prize.percentage / BigDecimal("100")).setScale(0, RoundingMode.HALF_DOWN)
                    left -= amount
                    FinalPrizeSummary(
                            position = place.position,
                            person = place.person,
                            amount = amount,
                    )
                }
            }.associateBy { it.person }
}