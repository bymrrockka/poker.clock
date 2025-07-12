package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Payout
import by.mrrockka.domain.TelegramGame
import by.mrrockka.response.builder.CalculationResponseBuilder
import by.mrrockka.service.calculation.CalculationService
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.PreCalculationValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class CalculationTelegramService(
        val calculationService: CalculationService,
        val gameTelegramFacadeService: GameTelegramFacadeService,
        val calculationValidator: PreCalculationValidator,
) {

    fun calculatePayouts(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        val telegramGame = gameTelegramFacadeService.getGameByMessageMetadata(messageMetadata)
        check(telegramGame != null) { "Game is not found for this chat" }
        calculationValidator.validateGame(telegramGame.game)

        val payouts = calculationService.calculateAndSave(telegramGame.game)
        check(payouts.isNotEmpty()) { "Payouts are not calculated." }

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text(telegramGame.payoutsResponse(payouts))
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}

private fun TelegramGame.payoutsResponse(payouts: List<Payout>): String {
    if (payouts.isEmpty()) return "Payouts are not calculated."
    return CalculationResponseBuilder(this.game, payouts).response()
}