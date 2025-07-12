package by.mrrockka.domain

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.Instant
import java.util.*

interface Game {
    val id: UUID
    val buyIn: BigDecimal
    val stack: BigDecimal?
    val finishedAt: Instant?
    val players: List<Player>
}

data class TournamentGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val finishedAt: Instant? = null,
        override val players: List<TournamentPlayer>,
        val finalePlaces: List<FinalPlace>? = emptyList(),
        val prizePool: List<PositionPrize>? = emptyList(),
) : Game

data class BountyTournamentGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        val bounty: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val finishedAt: Instant? = null,
        override val players: List<BountyPlayer>,
        val finalePlaces: List<FinalPlace>? = emptyList(),
        val prizePool: List<PositionPrize>? = emptyList(),
) : Game

data class CashGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val finishedAt: Instant? = null,
        override val players: List<CashPlayer>
) : Game

fun Game.toSummary(): List<PrizeSummary> {
    return when (this) {
        is TournamentGame -> prizeSummary(finalePlaces = finalePlaces, prizePool = prizePool, players.totalEntries())
        is BountyTournamentGame -> prizeSummary(finalePlaces = finalePlaces, prizePool = prizePool, players.totalEntries())
        is CashGame -> emptyList()
        else -> error("Unknown game type")
    }
}
