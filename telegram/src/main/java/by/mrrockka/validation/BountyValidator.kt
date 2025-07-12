package by.mrrockka.validation

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.takenToGiven
import org.springframework.stereotype.Component

@Component
class BountyValidator {
    fun validate(game: BountyTournamentGame, from: String, to: String) {
        check(from != to) { "Can't use same nickname @$from for bounty transaction" }

        val fromPlayer = game.players.find { it.person.nickname == from }
                ?: error("@$from person hadn't enter game")
        val toPlayer = game.players.find { it.person.nickname == to }
                ?: error("@$to person hadn't enter game")
        val fromEntries = fromPlayer.entries
        val toEntries = toPlayer.entries
        val (_, fromGiven) = fromPlayer.takenToGiven()
        val (_, toGiven) = toPlayer.takenToGiven()

        check(fromEntries.size - fromGiven.size > 0) { "@$from person knocked off from the game" }
        check(toEntries.size - toGiven.size > 0) { "@$to person knocked off from the game" }
    }
}