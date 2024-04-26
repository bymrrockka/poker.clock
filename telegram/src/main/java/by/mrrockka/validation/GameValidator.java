package by.mrrockka.validation;

import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.springframework.stereotype.Component;

@Component
public class GameValidator {

  public void validateGameIsTournamentType(final Game game) {
    if (!(game instanceof TournamentGame)) {
      throw new ProcessingRestrictedException(GameType.TOURNAMENT);
    }
  }

  public void validateGameIsCashType(final Game game) {
    if (!(game instanceof CashGame)) {
      throw new ProcessingRestrictedException(GameType.CASH);
    }
  }
}
