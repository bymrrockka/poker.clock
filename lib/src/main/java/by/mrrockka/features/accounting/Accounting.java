package by.mrrockka.features.accounting;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


//TODO: strategy to work with different types of games
@Service
@RequiredArgsConstructor
public class Accounting {

  private final List<CalculationStrategy> strategies;

  public List<Payout> calculate(final Game game) {
    return strategies.stream()
      .filter(strategy -> strategy.isApplicable(game))
      .findFirst()
      .orElseThrow()
      .calculate(game);
  }

}
