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
    private var buyin: BigDecimal? = null
    private var bounty: BigDecimal? = null
    private var moneys: List<BigDecimal>? = null
    private var entries: Int = 1

    init {
        init()
    }

    fun buyin(buyin: BigDecimal) {
        this.buyin = buyin
    }

    fun bounty(bounty: BigDecimal) {
        this.bounty = bounty
    }

    fun moneys(moneys: List<BigDecimal>) {
        this.moneys = moneys
    }

    fun entries(entries: Int) {
        this.entries = entries
    }

    fun tournament(): Player {
        return TournamentPlayer(
                person = person(),
                entries = (0..<entries).asSequence().map { buyin ?: defaultBuyin }.toList(),
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
                entries = (0..<entries).asSequence().map { buyin ?: defaultBuyin }.toList(),
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
                entries = (0..<entries).asSequence().map { buyin ?: defaultBuyin }.toList(),
        )
    }

    fun cashBatch(size: Int): List<CashPlayer> =
            (0..<size)
                    .asSequence()
                    .map { cash() }
                    .toList()

}

fun player(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init)
fun cashPlayer(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).cash()
fun tournamentPlayer(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).tournament()
fun bountyPlayer(init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).bounty()
fun cashPlayers(size: Int, init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).cashBatch(size)
fun tournamentPlayers(size: Int, init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).tournamentBatch(size)
fun bountyPlayers(size: Int, init: (PlayerBuilder.() -> Unit) = {}) = PlayerBuilder(init).bountyBatch(size)

infix operator fun <P : Player> P.plus(player: P): List<P> = listOf(this, player)
infix operator fun <P : Player> P.plus(players: Collection<P>): List<P> = listOf(this) + players
