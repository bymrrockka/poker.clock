package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Payout
import by.mrrockka.service.calculation.CalculationService
import by.mrrockka.validation.PreCalculationValidator
import org.springframework.stereotype.Service

@Service
class CalculationTelegramService(
        val calculationService: CalculationService,
        val gameTelegramFacadeService: GameTelegramService,
        val preCalculationValidator: PreCalculationValidator,
) {

    fun calculate(messageMetadata: MessageMetadata): List<Payout> {
        val telegramGame = gameTelegramFacadeService.findGame(messageMetadata)
        preCalculationValidator.validateGame(telegramGame.game)

        val payouts = calculationService.calculateAndSave(telegramGame.game)
        check(payouts.isNotEmpty()) { "Payouts are not calculated." }

        return payouts
    }
}