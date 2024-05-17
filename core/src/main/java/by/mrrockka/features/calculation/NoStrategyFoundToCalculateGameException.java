package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class NoStrategyFoundToCalculateGameException extends BusinessException {
  NoStrategyFoundToCalculateGameException(@NonNull final Game game) {
    super("No strategy found to calculate %s.".formatted(game.getClass().getSimpleName()));
  }
}
