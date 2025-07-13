package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.Randoms.Companion.sharedRandoms
import by.mrrockka.domain.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Suppress("UNCHECKED_CAST")
class GameBuilder(init: (GameBuilder.() -> Unit) = {}) {

    var randoms = sharedRandoms
    var id: UUID? = null
    var buyIn: BigDecimal? = null
    var stack: BigDecimal? = null
    var createdAt: Instant? = null
    var finishedAt: Instant? = null
    var players: List<Player> = emptyList()
    var finalePlaces: List<FinalPlace>? = emptyList()
    var prizePool: List<PositionPrize>? = emptyList()
    var bounty: BigDecimal? = null

    init {
        init()
    }

    fun prizeForFirst(): GameBuilder {
        this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
        this.finalePlaces = listOf(FinalPlace(1, this.players.first().person))
        return this
    }

    fun cash(): CashGame = CashGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            players = players as List<CashPlayer>,
            finishedAt = finishedAt,
            createdAt = createdAt ?: Instant.now(),
    )

    fun tournament(): TournamentGame = TournamentGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            players = players as List<TournamentPlayer>,
            finalePlaces = finalePlaces,
            prizePool = prizePool,
            createdAt = createdAt ?: Instant.now(),
            finishedAt = finishedAt,
    )

    fun bountyTournament(): BountyTournamentGame = BountyTournamentGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            players = players as List<BountyPlayer>,
            finalePlaces = finalePlaces,
            prizePool = prizePool,
            finishedAt = finishedAt,
            createdAt = createdAt ?: Instant.now(),
            bounty = bounty ?: BigDecimal("10"),
    )
}

fun game() = GameBuilder()
fun game(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init)
fun game(randoms: Randoms, init: (GameBuilder.() -> Unit) = {}) = GameBuilder { this.randoms = randoms }.also(init)
