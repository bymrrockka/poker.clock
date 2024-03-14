package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CalculationService {

  private final List<CalculationStrategy> strategies;

  public List<Payout> calculate(final Game game) {
    return strategies.stream()
      .filter(strategy -> strategy.isApplicable(game))
      .findFirst()
      .orElseThrow(() -> new NoStrategyFoundToCalculateGameException(game))
      .calculate(game);
  }

}
