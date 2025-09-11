package by.mrrockka.service

import by.mrrockka.domain.Bounty
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.takenToGiven
import by.mrrockka.parser.BountyMessageParser
import by.mrrockka.repo.BountyRepo
import org.springframework.stereotype.Service

@Service
class BountyTelegramService(
        private val bountyRepo: BountyRepo,
        private val bountyMessageParser: BountyMessageParser,
        private val gameService: GameTelegramService,
) {

    fun store(metadata: MessageMetadata): Bounty {
        metadata.checkMentions()
        val (from, to) = bountyMessageParser.parse(metadata)
        val telegramGame = gameService.findGame(metadata)
        check(telegramGame.game is BountyTournamentGame) { "Bounties can be submitted only for Bounty tournament" }

        validate(telegramGame.game, from, to)

        val players = telegramGame.game.players.associateBy { it.person.nickname }
        val fromPlayer = players[from]!!
        val toPlayer = players[to]!!

        val bounty = Bounty(from = fromPlayer.person, to = toPlayer.person, amount = telegramGame.game.bounty)
        bountyRepo.store(telegramGame.game.id, bounty, metadata.createdAt)

        return bounty
    }

    private fun validate(game: BountyTournamentGame, from: String, to: String) {
        check(from != to) { "You can't kick yourself off for bounty" }

        val fromPlayer = game.players.find { it.person.nickname == from }
                ?: error("@$from person hadn't enter game")
        val toPlayer = game.players.find { it.person.nickname == to }
                ?: error("@$to person hadn't enter game")
        val fromEntries = fromPlayer.entries
        val toEntries = toPlayer.entries
        val (_, fromGiven) = fromPlayer.takenToGiven()
        val (_, toGiven) = toPlayer.takenToGiven()

        check(fromEntries.size - fromGiven.size > 0) { "@$from knocked off from the game" }
        check(toEntries.size - toGiven.size > 0) { "@$to knocked off from the game" }
    }
}
