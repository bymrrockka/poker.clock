package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import by.mrrockka.sharedRandoms
import java.math.BigDecimal

internal val defaultBuyin = BigDecimal("10")

class PlayerBuilder(init: (PlayerBuilder.() -> Unit) = {}) {

    var randoms = sharedRandoms
    var buyin: BigDecimal? = null
    var bounty: BigDecimal? = null
    var entries: List<BigDecimal>? = null
    var size: Int = 1

    init {
        init()
    }

    fun tournament(): Player {
        return TournamentPlayer(
                person = person(randoms),
                entries = (0..<size).asSequence().map { buyin ?: defaultBuyin }.toList())
    }

    fun tournamentBatch(size: Int): List<Player> =
            (0..<size)
                    .asSequence()
                    .map { tournament() }
                    .toList()

}

fun player(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init)
fun player(randoms: Randoms, init: (PlayerBuilder.() -> Unit) = {}) = player().apply { this.randoms = randoms }.also(init)
