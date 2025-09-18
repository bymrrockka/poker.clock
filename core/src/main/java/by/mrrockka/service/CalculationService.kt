package by.mrrockka.service

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.TournamentGame
import by.mrrockka.repo.GameRepo
import by.mrrockka.repo.MoneyTransferRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface CalculationService {
    fun calculate(game: Game): List<Payout>
}

@Service
open class CalculationServiceImpl(
        private val calculator: GameCalculator,
        private val gameRepo: GameRepo,
        private val moneyTransferRepo: MoneyTransferRepo,
) : CalculationService {

    @Transactional(propagation = Propagation.REQUIRED)
    override fun calculate(game: Game): List<Payout> {
        val payouts = calculator.calculate(game)
        if (game.finishedAt == null) {
            gameRepo.store(game.finish())
        }
        moneyTransferRepo.store(game, payouts)
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
