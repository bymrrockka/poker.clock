package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalculationStrategyFactory {

  private final BountyCalculationStrategy bountyCalculationStrategy;
  private final CashCalculationStrategy cashCalculationStrategy;
  private final TournamentCalculationStrategy tournamentCalculationStrategy;

  public CalculationStrategy getStrategy(final Game game) {
    if (game.isType(BountyGame.class)) {
      return bountyCalculationStrategy;
    }

    if (game.isType(TournamentGame.class)) {
      return tournamentCalculationStrategy;
    }

    if (game.isType(CashGame.class)) {
      return cashCalculationStrategy;
    }

    throw new NoStrategyFoundToCalculateGameException(game);
  }

}
