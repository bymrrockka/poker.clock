package by.mrrockka.service

import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.game.TournamentGame
import by.mrrockka.parser.PrizePoolMessageParser
import by.mrrockka.service.exception.ChatGameNotFoundException
import by.mrrockka.service.exception.ProcessingRestrictedException
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.GameValidator
import by.mrrockka.validation.prizepool.PrizePoolValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
open class PrizePoolTelegramService(
        val prizePoolService: PrizePoolService,
        val prizePoolMessageParser: PrizePoolMessageParser,
        val gameTelegramFacadeService: GameTelegramFacadeService,
        val gameValidator: GameValidator,
        val prizePoolValidator: PrizePoolValidator,
) {
    fun storePrizePool(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        val prizePool = prizePoolMessageParser.parse(messageMetadata.text)
        prizePoolValidator.validate(prizePool)

        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(messageMetadata)
                .orElseThrow { ChatGameNotFoundException() }
        gameValidator.validateGameIsTournamentType(telegramGame.game)

        if (telegramGame.game !is TournamentGame) {
            throw ProcessingRestrictedException(GameType.TOURNAMENT)
        }

        prizePoolService.store(telegramGame.game.getId(), prizePool)
        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("""
                    Prize pool stored:
                    ${prizePool.positionAndPercentages.joinToString { "${it.position}. -> ${it.percentage}%" }}
                """.trimIndent())
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}
