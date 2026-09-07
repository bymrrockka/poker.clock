package by.mrrockka.scenario.common

import by.mrrockka.Command
import by.mrrockka.GivenSpecification
import by.mrrockka.domain.GameType
import by.mrrockka.scenario.common.Commands.Companion.entries
import by.mrrockka.scenario.common.Commands.Companion.finalePlaces
import by.mrrockka.scenario.common.Commands.Companion.game
import by.mrrockka.scenario.common.Commands.Companion.prizePool
import by.mrrockka.service.up
import java.math.BigDecimal

fun GivenSpecification.createGameFlow(gameType: GameType, buyin: BigDecimal, players: List<String> = emptyList(), replyTo: Command? = null): Command.BotMessage {
    val toDelete = mutableListOf<Command>()
    user { game }
    toDelete += bot { "Type of game?" }
    toDelete += user { gameType.title }
    toDelete += bot { "Buyin?" }
    toDelete += user { buyin.up().toString() }
    if (gameType == GameType.BOUNTY) {
        toDelete += bot { "Bounty?" }
        toDelete += user { buyin.up().toString() }
    }
    toDelete += bot { "Players?" }
    toDelete += user(replyTo = replyTo) { players.entries() }
    val game = bot { "Game created" }
    game.pinned()
    toDelete.deleted()
    return game
}

fun GivenSpecification.finalePlacesFlow(vararg places: Pair<Int, String>) = finalePlacesFlow { places.toMap() }

fun GivenSpecification.finalePlacesFlow(placesProvider: GivenSpecification.() -> Map<Int, String>): Command.BotMessage {
    with(placesProvider()) {
        val toDelete = mutableListOf<Command>()
        user { finalePlaces }
        toDelete += bot { "Pool size?" }
        toDelete += user { "$size" }
        forEach { (place, nickname) ->
            toDelete += bot { "$place place" }
            toDelete += user { "@$nickname" }
        }
        val summary = bot { "Finale places stored" }
        summary.pinned()
        toDelete.deleted()
        return summary
    }
}

fun GivenSpecification.prizePoolFlow(vararg places: Pair<Int, Int>) = prizePoolFlow { places.toMap() }

fun GivenSpecification.prizePoolFlow(placesProvider: GivenSpecification.() -> Map<Int, Int>): Command.BotMessage {
    with(placesProvider()) {
        val toDelete = mutableListOf<Command>()
        user { prizePool }
        toDelete += bot { "Pool size?" }
        toDelete += user { "$size" }
        forEach { (place, percentage) ->
            toDelete += bot { "$place place" }
            toDelete += user { "$percentage" }
        }
        val summary = bot { "Prize pool stored" }
        summary.pinned()
        toDelete.deleted()
        return summary
    }
}
