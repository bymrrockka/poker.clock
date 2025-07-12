package by.mrrockka.service

import by.mrrockka.domain.Bounty
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.BountyMessageParser
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.BountyValidator
import by.mrrockka.validation.mentions.PersonMentionsValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class BountyTelegramService(
        private val bountyService: BountyService,
        private val bountyMessageParser: BountyMessageParser,
        private val gameTelegramFacadeService: GameTelegramFacadeService,
        private val personMentionsValidator: PersonMentionsValidator,
        private val bountyValidator: BountyValidator,
) {

    fun storeBounty(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        personMentionsValidator.validateMessageMentions(messageMetadata, 2)
        val (from, to) = bountyMessageParser.parse(messageMetadata)
        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(messageMetadata)

        check(telegramGame != null) { "Game is not found for this chat" }
        val game = telegramGame.game as BountyTournamentGame
        bountyValidator.validate(game, from, to)
        val fromPlayer = game.players.find { it.person.nickname == from }!!
        val toPlayer = game.players.find { it.person.nickname == to }!!

        val bounty = Bounty(from = fromPlayer.person, to = toPlayer.person, amount = game.bounty)
        bountyService.storeBounty(game.id, bounty, messageMetadata.createdAt)

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("Bounty amount ${game.bounty} from @$from stored for @$to")
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}
