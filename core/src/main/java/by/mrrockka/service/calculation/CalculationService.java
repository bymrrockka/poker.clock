package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.service.GameService;
import by.mrrockka.service.MoneyTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CalculationService {

  private final MoneyTransferService moneyTransferService;
  private final GameService gameService;
  private final CalculationStrategyFactory calculationStrategyFactory;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<Payout> calculateAndSave(final Game game) {
    final var payouts = calculationStrategyFactory.getStrategy(game).calculate(game);

    if (gameService.doesGameHasUpdates(game)) {
      //  todo: add transactionality service to execute transaction only part of code
      gameService.finishGame(game);
      moneyTransferService.storeBatch(game, payouts);
    }
    return payouts;
  }

}
