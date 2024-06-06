package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class NoStrategyFoundToCalculateGameException extends BusinessException {
  NoStrategyFoundToCalculateGameException(@NonNull final String strategyName, @NonNull final Game game) {
    super("No strategy with name %s found to calculate %s.".formatted(strategyName, game.getClass().getSimpleName()));
  }
}
