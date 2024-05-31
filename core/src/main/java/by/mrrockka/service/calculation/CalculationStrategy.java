package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;

import java.util.List;

public interface CalculationStrategy {

  List<Payout> calculate(final Game game);

}
