package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.service.MoneyTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CalculationService {

  private final List<CalculationStrategy> strategies;
  private final MoneyTransferService moneyTransferService;

  public List<Payout> calculate(final Game game) {
    final var payouts = strategies.stream()
      .filter(strategy -> strategy.isApplicable(game))
      .findFirst()
      .orElseThrow(() -> new NoStrategyFoundToCalculateGameException(game))
      .calculate(game);

    moneyTransferService.storeBatch(game.getId(), payouts);
    return payouts;
  }

}
