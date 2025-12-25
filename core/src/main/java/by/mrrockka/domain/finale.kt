package by.mrrockka.domain

import java.math.BigDecimal
import java.math.RoundingMode

interface GameSummary {
    val person: Person
    val buyIn: BigDecimal

    fun total(): BigDecimal
    fun entries(): BigDecimal
}

interface PrizeGameSummary : GameSummary {
    val entriesNum: Int
    val position: Int?
    val prize: BigDecimal
}

data class TournamentSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val position: Int? = null,
        override val prize: BigDecimal,
) : PrizeGameSummary {
    override fun total(): BigDecimal = prize - entries()
    override fun entries(): BigDecimal = buyIn * BigDecimal(entriesNum)
}

data class BountyTournamentSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val prize: BigDecimal,
        override val position: Int? = null,
        val bounty: BountySummary,
) : PrizeGameSummary {
    override fun total(): BigDecimal = bounty.total + prize - entries()
    override fun entries(): BigDecimal = buyIn * BigDecimal(entriesNum)
}

data class BountySummary(
        val amount: BigDecimal,
        val takenNum: Int,
        val givenNum: Int,
) {
    val given: BigDecimal by lazy { BigDecimal(givenNum) * amount }
    val taken: BigDecimal by lazy { BigDecimal(takenNum) * amount }
    val total: BigDecimal by lazy { taken - given }
}

data class CashSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        val withdrawals: BigDecimal,
) : GameSummary {
    override fun total(): BigDecimal = withdrawals - entries()
    override fun entries(): BigDecimal = buyIn
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
                buyIn = buyIn,
                entriesNum = player.entries.size,
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
        )
    }
}

fun BountyTournamentGame.gameSummary(): List<BountyTournamentSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizePool!!.prizeSummary(finalePlaces!!, players.flatMap { it.entries }.total())

    return players.map { player ->
        val prize = prizeSummary[player.person]
        val (taken, given) = player.takenToGiven()
        BountyTournamentSummary(
                person = player.person,
                buyIn = buyIn,
                entriesNum = player.entries.size,
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
                bounty = BountySummary(
                        amount = bounty,
                        takenNum = taken.size,
                        givenNum = given.size,
                ),
        )
    }
}

fun CashGame.gameSummary(): List<CashSummary> = players.map { player ->
    CashSummary(
            person = player.person,
            buyIn = player.entries.total(),
            withdrawals = player.withdrawals.total(),
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