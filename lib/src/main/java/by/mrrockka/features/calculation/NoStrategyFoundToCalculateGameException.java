package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.exception.ErrorCodes.NO_STRATEGY_FOUND_TO_CALCULATE_GAME;

public class NoStrategyFoundToCalculateGameException extends BusinessException {
  public NoStrategyFoundToCalculateGameException(@NonNull final Game game) {
    super("No strategy found to calculate %s.".formatted(game.getClass().getSimpleName()),
          NO_STRATEGY_FOUND_TO_CALCULATE_GAME);
  }
}
