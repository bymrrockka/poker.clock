package by.mrrockka.service

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Game
import by.mrrockka.domain.PositionPrize
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.takenToGiven
import by.mrrockka.domain.total
import by.mrrockka.domain.totalEntries
import by.mrrockka.domain.totalWithdrawals
import java.math.BigDecimal
import java.math.RoundingMode

fun Game.toTournamentSummary(): List<PlayerPrizeSummary> = toSummary()
        .filter { it is PlayerPrizeSummary }
        .map { it as PlayerPrizeSummary }

fun Game.toSummary(): List<PlayerSummary> {
    return when (this) {
        is TournamentGame -> playerSummary()
        is BountyTournamentGame -> playerSummary()
        is CashGame -> playerSummary()
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

internal data class FinalPrizeSummary(val position: Int, val amount: BigDecimal, val person: BasicPerson)

interface PlayerSummary {
    val person: BasicPerson
    val buyIn: BigDecimal

    fun total(): BigDecimal
    fun entries(): BigDecimal
}

interface PlayerPrizeSummary : PlayerSummary {
    val entriesNum: Int
    val position: Int?
    val prize: BigDecimal
}

data class TournamentPlayerSummary(
        override val person: BasicPerson,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val position: Int? = null,
        override val prize: BigDecimal,
) : PlayerPrizeSummary {
    override fun total(): BigDecimal = prize - entries()
    override fun entries(): BigDecimal = buyIn * BigDecimal(entriesNum)
}

data class BountyTournamentPlayerSummary(
        override val person: BasicPerson,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val prize: BigDecimal,
        override val position: Int? = null,
        val bounty: BountySummary,
) : PlayerPrizeSummary {
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

data class CashPlayerSummary(
        override val person: BasicPerson,
        override val buyIn: BigDecimal,
        val withdrawals: BigDecimal,
) : PlayerSummary {
    override fun total(): BigDecimal = withdrawals - entries()
    override fun entries(): BigDecimal = buyIn
}

fun TournamentGame.playerSummary(): List<TournamentPlayerSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizeSummary(prizePool!!, finalePlaces!!)

    return players.map { player ->
        val prize = prizeSummary[player.person]
        TournamentPlayerSummary(
                person = player.person,
                buyIn = buyIn,
                entriesNum = player.entries.size,
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
        )
    }
}

fun BountyTournamentGame.playerSummary(): List<BountyTournamentPlayerSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizeSummary(prizePool!!, finalePlaces!!)

    return players.map { player ->
        val prize = prizeSummary[player.person]
        val (taken, given) = player.takenToGiven()
        BountyTournamentPlayerSummary(
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

fun CashGame.playerSummary(): List<CashPlayerSummary> = players.map { player ->
    CashPlayerSummary(
            person = player.person,
            buyIn = player.entries.total(),
            withdrawals = player.withdrawals.total(),
    )
}

private fun Game.prizeSummary(prizePoll: List<PositionPrize>, finalePlaces: List<FinalPlace>): Map<BasicPerson, FinalPrizeSummary> {
    val total = players.totalEntries()
    var left = total
    return prizePoll.sortedBy { it.position }
            .zip(finalePlaces.sortedBy { it.position })
            .mapIndexed { index, (prize, place) ->
                if (finalePlaces.size - 1 <= index) FinalPrizeSummary(
                        position = place.position,
                        person = place.person,
                        amount = left,
                )
                else {
                    val amount = (total * prize.percentage / BigDecimal("100")).scaleDown()
                    left -= amount
                    FinalPrizeSummary(
                            position = place.position,
                            person = place.person,
                            amount = amount,
                    )
                }
            }.associateBy { it.person }
}

fun BigDecimal.scaleDown(): BigDecimal = setScale(0, RoundingMode.HALF_DOWN)
