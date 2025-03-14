package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TelegramGame
import by.mrrockka.domain.game.CashGame
import by.mrrockka.domain.payout.Payout
import by.mrrockka.service.calculation.CalculationService
import by.mrrockka.service.exception.ChatGameNotFoundException
import by.mrrockka.service.exception.PayoutsAreNotCalculatedException
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.calculation.CalculationValidator
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.function.Supplier

@Slf4j
@Service
class CalculationTelegramService(
        val calculationService: CalculationService,
        val gameTelegramFacadeService: GameTelegramFacadeService,
        val calculationValidator: CalculationValidator,
) {

    fun calculatePayouts(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(messageMetadata)
                .orElseThrow<ChatGameNotFoundException?>(Supplier { ChatGameNotFoundException() })

        calculationValidator.validateGame(telegramGame.game)

        val payouts = calculationService.calculateAndSave(telegramGame.game)
        if (payouts.isEmpty()) {
            throw PayoutsAreNotCalculatedException()
        }

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text(telegramGame.payoutsResponse(payouts))
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}

private fun TelegramGame.payoutsResponse(payouts: List<Payout>): String {
    if (payouts.isEmpty()) return ""

    return when (this.game) {
        is CashGame -> payouts.joinToString(separator = "\n") {
            """
            -----------------------------
            Payout to: @${it.person().nickname}
                Entries: ${it.personEntries.total()}
                Withdrawals: ${it.personWithdrawals.total()}
                Total: ${it.total()}
                
            """.trimIndent()
        }

        else -> ""
    }
}