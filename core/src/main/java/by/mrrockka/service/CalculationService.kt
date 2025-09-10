package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface CalculationService {
    fun calculateAndSave(game: Game): List<Payout>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class CalculationServiceImpl(
        val calculator: GameCalculator,
        val gameService: GameServiceOld,
        val moneyTransferService: MoneyTransferService,
) : CalculationService {

    override fun calculateAndSave(game: Game): List<Payout> {
        val payouts = calculator.calculate(game)
        if (game.finishedAt == null) {
            gameService.finishGame(game)
            moneyTransferService.storeBatch(game, payouts)
        }
        return payouts
    }
}
