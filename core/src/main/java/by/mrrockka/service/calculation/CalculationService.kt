package by.mrrockka.service.calculation

import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.service.GameService
import by.mrrockka.service.MoneyTransferService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class CalculationService(
        val calculator: GameCalculator,
        val gameService: GameService,
        val moneyTransferService: MoneyTransferService,
) {
    @Transactional(propagation = Propagation.REQUIRED)
    open fun calculateAndSave(game: Game): List<Payout> {
        val payouts = calculator.calculate(game)
        if (gameService.retrieveGame(game.id).finishedAt == null) {
            gameService.finishGame(game)
            moneyTransferService.storeBatch(game, payouts)
        }
        return payouts
    }
}
