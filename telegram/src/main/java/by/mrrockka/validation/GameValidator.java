package by.mrrockka.validation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.springframework.stereotype.Component;

@Component
public class GameValidator {

  public void validateGameIsTournamentType(final Game game) {
    if (!(game instanceof TournamentGame)) {
      throw new ProcessingRestrictedException("%s or %s".formatted(GameType.TOURNAMENT, GameType.BOUNTY));
    }
  }

}
