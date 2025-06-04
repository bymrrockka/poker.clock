package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.domain.*
import by.mrrockka.sharedRandoms
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Suppress("UNCHECKED_CAST")
class GameBuilder(init: (GameBuilder.() -> Unit) = {}) {

    var randoms = sharedRandoms
    var id: UUID? = null
    var buyIn: BigDecimal? = null
    var stack: BigDecimal? = null
    var finishedAt: Instant? = null
    var players: List<Player> = emptyList()
    var finalePlaces: List<FinalPlace>? = emptyList()
    var prizePool: List<PositionPrize>? = emptyList()
    var bounty: BigDecimal? = null

    init {
        init()
    }

    fun tournament(): TournamentGame = TournamentGame(
            id = id ?: randoms.uuid(),
            buyIn = buyIn ?: BigDecimal("10"),
            stack = stack ?: randoms.decimal(from = 1000, to = 30000),
            players = players as List<TournamentPlayer>,
            finalePlaces = finalePlaces,
            prizePool = prizePool,
            finishedAt = finishedAt,
    )

}

fun game(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init)
fun game(randoms: Randoms, init: (GameBuilder.() -> Unit) = {}) = game().apply { this.randoms = randoms }.also(init)
