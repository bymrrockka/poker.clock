package by.mrrockka.features.calculation;

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

  private final List<CalculationStrategy> strategies;
  private final MoneyTransferService moneyTransferService;
  private final GameService gameService;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<Payout> calculateAndSave(final Game game) {
    final var payouts = strategies.stream()
      .filter(strategy -> strategy.isApplicable(game))
      .findFirst()
      .orElseThrow(() -> new NoStrategyFoundToCalculateGameException(game))
      .calculate(game);

    if (gameService.doesGameHasUpdates(game)) {
      //  todo: add transactionality service to execute transaction only part of code
      gameService.finishGame(game);
      moneyTransferService.storeBatch(game, payouts);
    }
    return payouts;
  }

}
