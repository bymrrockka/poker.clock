package by.mrrockka.service.calculation

import by.mrrockka.domain.game.Game
import by.mrrockka.domain.payout.Payout
import by.mrrockka.service.GameService
import by.mrrockka.service.MoneyTransferService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
open class CalculationService(
        val moneyTransferService: MoneyTransferService,
        val gameService: GameService,
        val calculationStrategyFactory: CalculationStrategyFactory,
) {
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    open fun calculateAndSave(game: Game): List<Payout> {
        val payouts = calculationStrategyFactory.getStrategy(game).calculate(game)
//todo: refactor this check
        if (gameService.doesGameHasUpdates(game)) {
            //  todo: add transactionality service to execute transaction only part of code
            gameService.finishGame(game)
            moneyTransferService.storeBatch(game, payouts)
        }
        return payouts
    }
}
