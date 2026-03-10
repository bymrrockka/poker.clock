package by.mrrockka.service

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Game
import by.mrrockka.domain.PositionPrize
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.takenToGiven
import by.mrrockka.domain.total
import by.mrrockka.feature.ServiceFeeFeature
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PlayerSummaryService(
        private val serviceFeeFeature: ServiceFeeFeature,
) {
    fun tournamentSummary(game: Game): List<PlayerPrizeSummary> = summary(game)
            .filter { it is PlayerPrizeSummary }
            .map { it as PlayerPrizeSummary }

    fun summary(game: Game): List<PlayerSummary> {
        val serviceFeeAmount = serviceFeeFeature.calculate(game.total())
        val compressedTotal = game.total() - serviceFeeAmount
        return when (game) {
            is TournamentGame -> game.playerSummary(compressedTotal)
            is BountyTournamentGame -> game.playerSummary(compressedTotal)
            is CashGame -> game.playerSummary(compressedTotal)
            else -> error("Unknown game type")
        }
    }

    private fun TournamentGame.playerSummary(compressedTotal: BigDecimal): List<TournamentPlayerSummary> {
        checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
        checkNotNull(prizePool) { "Can't calculate with no prize pool" }

        val prizeSummary = compressedTotal.prizeSummary(prizePool!!, finalePlaces!!)

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

    private fun BountyTournamentGame.playerSummary(compressedTotal: BigDecimal): List<BountyTournamentPlayerSummary> {
        checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
        checkNotNull(prizePool) { "Can't calculate with no prize pool" }

        val (prizeTotal, bountyTotal) = compressedTotal.let { amount ->
            val halfDown = (amount.setScale(2) / 2.toBigDecimal()).halfDown()
            amount - halfDown to halfDown
        }

        val prizeSummary = prizeTotal.prizeSummary(prizePool!!, finalePlaces!!)
        val bountySummary = bountyTotal.bountySummary(players, bounty)

        return players.map { player ->
            val prize = prizeSummary[player.person]
            val bounty = bountySummary[player.person] ?: error("Bounty summary for ${player.person} not found")
            BountyTournamentPlayerSummary(
                    person = player.person,
                    buyIn = buyIn,
                    entriesNum = player.entries.size,
                    prize = prize?.amount ?: BigDecimal.ZERO,
                    position = prize?.position,
                    bounty = bounty,
            )
        }
    }

    private fun BigDecimal.bountySummary(players: List<BountyPlayer>, bounty: BigDecimal): Map<BasicPerson, BountySummary> {
        val average = (this.setScale(1) / players.flatMap { it.entries }.size.toBigDecimal()).up()
        //last player safes his bounty
        val state = AmountState(this - bounty)

        return players
                .mapIndexed { index, player ->
                    val (taken, given) = player.takenToGiven()
                    val takenCompressed = taken.size.toBigDecimal() * average
                    player.person to BountySummary(
                            total = (if (taken.size - given.size > 0) state.decreaseAndGet(takenCompressed) else taken.total()) - given.total(),
                            taken = taken.size,
                            given = given.size,
                    )
                }.toMap()
    }

    private fun CashGame.playerSummary(compressedTotal: BigDecimal): List<CashPlayerSummary> {
        val ratio = compressedTotal.setScale(2) / total()
        val state = AmountState(compressedTotal)

        return players.mapIndexed { index, player ->
            CashPlayerSummary(
                    person = player.person,
                    buyIn = player.entries.total(),
                    withdrawals = if (index == players.size - 1) state.all() else state.decreaseAndGet(player.withdrawals.total() * ratio),
            )
        }
    }

    private fun BigDecimal.prizeSummary(prizePoll: List<PositionPrize>, finalePlaces: List<FinalPlace>): Map<BasicPerson, FinalPrizeSummary> {
        val state = AmountState(this)
        return prizePoll.sortedBy { it.position }
                .zip(finalePlaces.sortedBy { it.position })
                .mapIndexed { index, (prize, place) ->
                    val amount = this * prize.percentage / BigDecimal("100.0")
                    FinalPrizeSummary(
                            position = place.position,
                            person = place.person,
                            amount = if (index == prizePoll.size - 1) state.all() else state.decreaseAndGet(amount),
                    )
                }.associateBy { it.person }
    }

}

fun BigDecimal.down(): BigDecimal = setScale(0, RoundingMode.DOWN)
fun BigDecimal.halfDown(): BigDecimal = setScale(0, RoundingMode.HALF_DOWN)
fun BigDecimal.up(): BigDecimal = setScale(0, RoundingMode.UP)

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
    override fun entries(): BigDecimal = buyIn * entriesNum.toBigDecimal()
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
    override fun entries(): BigDecimal = buyIn * entriesNum.toBigDecimal()
}

data class BountySummary(
        val total: BigDecimal,
        val taken: Int,
        val given: Int,
)

data class CashPlayerSummary(
        override val person: BasicPerson,
        override val buyIn: BigDecimal,
        val withdrawals: BigDecimal,
) : PlayerSummary {
    override fun total(): BigDecimal = withdrawals - entries()
    override fun entries(): BigDecimal = buyIn
}

private data class FinalPrizeSummary(val position: Int, val amount: BigDecimal, val person: BasicPerson)
data class AmountState(private var state: BigDecimal) {
    fun decreaseAndGet(amount: BigDecimal): BigDecimal {
        val scaled = amount.up()
        return when {
            amount < BigDecimal.ZERO -> amount
            state - scaled == BigDecimal.ONE -> all()
            scaled < state -> {
                state -= scaled
                scaled
            }

            scaled >= state -> {
                val left = state
                state = BigDecimal.ZERO
                left
            }

            else -> error("No more left to decrease")
        }
    }

    fun all(): BigDecimal {
        val all = state
        state = BigDecimal.ZERO
        return all
    }
}

