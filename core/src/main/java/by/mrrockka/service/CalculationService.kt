package by.mrrockka.service

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.TournamentGame
import by.mrrockka.repo.GameRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface CalculationService {
    fun calculateAndSave(game: Game): List<Payout>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class CalculationServiceImpl(
        val calculator: GameCalculator,
        val gameRepo: GameRepo,
        val moneyTransferService: MoneyTransferService,
) : CalculationService {

    override fun calculateAndSave(game: Game): List<Payout> {
        val payouts = calculator.calculate(game)
        if (game.finishedAt == null) {
            gameRepo.update(game.finish())
        }
        moneyTransferService.storeBatch(game, payouts)
        return payouts
    }

    private fun Game.finish(): Game {
        return when (this) {
            is CashGame -> this.copy(finishedAt = Instant.now())
            is BountyTournamentGame -> this.copy(finishedAt = Instant.now())
            is TournamentGame -> this.copy(finishedAt = Instant.now())
            else -> error("Game type is not supported")
        }
    }
}
