package by.mrrockka.domain

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.Instant
import java.util.*

interface Game {
    val id: UUID
    val buyIn: BigDecimal
    val stack: BigDecimal?
    val createdAt: Instant
    val finishedAt: Instant?
    val players: List<Player>
}

enum class GameType {
    CASH, TOURNAMENT, BOUNTY
}

data class TournamentGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val createdAt: Instant,
        override val finishedAt: Instant? = null,
        val playersProvider: () -> List<TournamentPlayer>,
        private val finalePlacesProvider: () -> List<FinalPlace>? = { null },
        private val prizePoolProvider: () -> List<PositionPrize>? = { null },
) : Game {
    override val players: List<TournamentPlayer> by lazy { playersProvider() }
    val finalePlaces: List<FinalPlace>? by lazy { finalePlacesProvider() }
    val prizePool: List<PositionPrize>? by lazy { prizePoolProvider() }
}

data class BountyTournamentGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        val bounty: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val createdAt: Instant,
        override val finishedAt: Instant? = null,
        val playersProvider: () -> List<BountyPlayer>,
        private val finalePlacesProvider: () -> List<FinalPlace>? = { null },
        private val prizePoolProvider: () -> List<PositionPrize>? = { null },
) : Game {
    override val players: List<BountyPlayer> by lazy { playersProvider() }
    val finalePlaces: List<FinalPlace>? by lazy { finalePlacesProvider() }
    val prizePool: List<PositionPrize>? by lazy { prizePoolProvider() }
}

data class CashGame(
        override val id: UUID,
        override val buyIn: BigDecimal,
        override val stack: BigDecimal? = ZERO,
        override val createdAt: Instant,
        override val finishedAt: Instant? = null,
        val playersProvider: () -> List<CashPlayer>,
) : Game {
    override val players: List<CashPlayer> by lazy { playersProvider() }
}

fun Game.toSummary(): List<PrizeSummary> {
    return when (this) {
        is TournamentGame -> prizeSummary(finalePlaces = finalePlaces, prizePool = prizePool, players.totalEntries())
        is BountyTournamentGame -> prizeSummary(finalePlaces = finalePlaces, prizePool = prizePool, players.totalEntries())
        is CashGame -> emptyList()
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
