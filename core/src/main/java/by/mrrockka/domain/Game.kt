package by.mrrockka.domain

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
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
    CASH, TOURNAMENT, BOUNTY;

    val title = this.name.lowercase().capitalize()
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

fun Game.toTournamentSummary(): List<PrizeGameSummary> = toSummary()
        .filter { it is PrizeGameSummary }
        .map { it as PrizeGameSummary }

fun Game.toSummary(): List<GameSummary> {
    return when (this) {
        is TournamentGame -> gameSummary()
        is BountyTournamentGame -> gameSummary()
        is CashGame -> gameSummary()
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

fun game(
        type: GameType,
        buyin: BigDecimal,
        stack: BigDecimal = BigDecimal.TEN,
        bounty: BigDecimal?,
        createdAt: Instant,
): Game {
    return when (type) {
        GameType.TOURNAMENT ->
            TournamentGame(
                    id = UUID.randomUUID(),
                    buyIn = buyin.defaultScale(),
                    stack = stack.defaultScale(),
                    playersProvider = { emptyList() },
                    createdAt = createdAt,
            )

        GameType.CASH ->
            CashGame(
                    id = UUID.randomUUID(),
                    buyIn = buyin.defaultScale(),
                    stack = stack.defaultScale(),
                    playersProvider = { emptyList() },
                    createdAt = createdAt,
            )

        GameType.BOUNTY -> {
            check(bounty != null) { "Bounty should be specified" }
            BountyTournamentGame(
                    id = UUID.randomUUID(),
                    buyIn = buyin.defaultScale(),
                    stack = stack.defaultScale(),
                    bounty = bounty.defaultScale(),
                    playersProvider = { emptyList() },
                    createdAt = createdAt,
            )
        }
    }
}
