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

    var id: UUID? = null
    var buyIn: BigDecimal? = null
    var stack: BigDecimal? = null
    var createdAt: Instant? = null
    var finishedAt: Instant? = null
    var players: List<Player> = emptyList()
    var finalePlaces: List<FinalPlace> = emptyList()
    var prizePool: List<PositionPrize> = emptyList()
    var bounty: BigDecimal? = null

    init {
        init()
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

fun game() = GameBuilder()
fun game(init: (GameBuilder.() -> Unit) = {}) = GameBuilder(init)
