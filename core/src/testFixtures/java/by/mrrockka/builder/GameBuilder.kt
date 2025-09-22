package by.mrrockka.builder

import by.mrrockka.CoreRandoms
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Player
import by.mrrockka.domain.PositionPrize
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.TournamentPlayer
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Suppress("UNCHECKED_CAST")
class GameBuilder(init: (GameBuilder.() -> Unit) = {}) : AbstractBuilder<CoreRandoms>(coreRandoms) {

    private var id: UUID? = null
    private var buyIn: BigDecimal? = null
    private var stack: BigDecimal? = null
    private var createdAt: Instant? = null
    private var finishedAt: Instant? = null
    private var players: List<Player> = emptyList()
    private var finalePlaces: List<FinalPlace> = emptyList()
    private var prizePool: List<PositionPrize> = emptyList()
    private var bounty: BigDecimal? = null

    init {
        init()
    }

    fun id(id: UUID) {
        this.id = id
    }

    fun buyIn(buyIn: BigDecimal) {
        this.buyIn = buyIn
    }

    fun stack(stack: BigDecimal) {
        this.stack = stack
    }

    fun createdAt(createdAt: Instant) {
        this.createdAt = createdAt
    }

    fun finishedAt(finishedAt: Instant) {
        this.finishedAt = finishedAt
    }

    fun players(players: List<Player>) {
        this.players = players
    }

    fun finalePlaces(finalePlaces: List<FinalPlace>) {
        this.finalePlaces = finalePlaces
    }

    fun finalePlaces(vararg finalePlaces: FinalPlace) {
        this.finalePlaces = finalePlaces.toList()
    }

    fun prizePool(prizePool: List<PositionPrize>) {
        this.prizePool = prizePool
    }

    fun prizePool(vararg prizePool: PositionPrize) {
        this.prizePool = prizePool.toList()
    }

    fun bounty(bounty: BigDecimal) {
        this.bounty = bounty
    }

    fun cash(): CashGame = CashGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            playersProvider = { players as List<CashPlayer> },
            finishedAt = finishedAt,
            createdAt = createdAt ?: Instant.now(),
    )

    fun tournament(): TournamentGame = TournamentGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            playersProvider = { players as List<TournamentPlayer> },
            finalePlacesProvider = { finalePlaces },
            prizePoolProvider = { prizePool },
            createdAt = createdAt ?: Instant.now(),
            finishedAt = finishedAt,
    )

    fun bountyTournament(): BountyTournamentGame = BountyTournamentGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            playersProvider = { players as List<BountyPlayer> },
            finalePlacesProvider = { finalePlaces },
            prizePoolProvider = { prizePool },
            finishedAt = finishedAt,
            createdAt = createdAt ?: Instant.now(),
            bounty = bounty ?: BigDecimal("10"),
    )
}

fun game(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init)
fun bountyGame(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init).bountyTournament()
fun tournamentGame(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init).tournament()
fun cashGame(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init).cash()
