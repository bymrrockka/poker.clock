package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.PrizePoolMessageParser
import by.mrrockka.validation.prizepool.PrizePoolValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
open class PrizePoolTelegramService(
        val prizePoolService: PrizePoolService,
        val prizePoolMessageParser: PrizePoolMessageParser,
        val gameTelegramFacadeService: GameTelegramService,
        val prizePoolValidator: PrizePoolValidator,
) {
    fun storePrizePool(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        val prizePool = prizePoolMessageParser.parse(messageMetadata.text)
        prizePoolValidator.validate(prizePool)

        val telegramGame = gameTelegramFacadeService
                .findGame(messageMetadata)
        check(telegramGame.game !is CashGame) { "Finale places is not allowed for cash game" }
        prizePoolService.store(telegramGame.game.id, prizePool)
        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()
            text = """
                    Prize pool stored:
                    ${prizePool.positionPrizes.joinToString { "${it.position}. -> ${it.percentage}%" }}
                """.trimIndent()
            replyToMessageId = telegramGame.messageMetadata.id.toInt()
        }
    }
}
