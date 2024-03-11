package by.mrrockka.features.accounting;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;

import java.util.List;

public interface CalculationStrategy {

  List<Payout> calculate(final Game game);

  boolean isApplicable(final Game game);

  default <T extends Game> T castToType(final Game game) {
    return (T) game;
  }
}
