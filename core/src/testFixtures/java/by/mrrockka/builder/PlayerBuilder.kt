package by.mrrockka.builder

import by.mrrockka.CoreRandoms
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import java.math.BigDecimal

internal val defaultBuyin = BigDecimal("10")

class PlayerBuilder(init: (PlayerBuilder.() -> Unit) = {}) : AbstractBuilder<CoreRandoms>(coreRandoms) {
    var buyin: BigDecimal? = null
    var bounty: BigDecimal? = null
    var entries: List<BigDecimal>? = null
    var entriesSize: Int = 1

    init {
        init()
    }

    fun tournament(): Player {
        return TournamentPlayer(
                person = person(),
                entries = (0..<entriesSize).asSequence().map { buyin ?: defaultBuyin }.toList(),
        )
    }

    fun tournamentBatch(size: Int): List<Player> =
            (0..<size)
                    .asSequence()
                    .map { tournament() }
                    .toList()

    fun bounty(): BountyPlayer {
        return BountyPlayer(
                person = person(),
                entries = (0..<entriesSize).asSequence().map { buyin ?: defaultBuyin }.toList(),
        )
    }

    fun bountyBatch(size: Int): List<BountyPlayer> =
            (0..<size)
                    .asSequence()
                    .map { bounty() }
                    .toList()

    fun cash(): CashPlayer {
        return CashPlayer(
                person = person(),
                entries = (0..<entriesSize).asSequence().map { buyin ?: defaultBuyin }.toList(),
        )
    }

    fun cashBatch(size: Int): List<CashPlayer> =
            (0..<size)
                    .asSequence()
                    .map { cash() }
                    .toList()

}

fun player(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init)
