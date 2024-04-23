package by.mrrockka.validation.finaleplaces;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FinalePlacesValidator {

  public void validateGameType(final Game game) {
    if (!(game instanceof TournamentGame)) {
      throw new ProcessingRestrictedException("%s or %s".formatted(GameType.TOURNAMENT, GameType.BOUNTY));
    }
  }

  public void validatePlaces(final Map<Integer, TelegramPerson> places) {
    if (places.isEmpty()) {
      throw new FinalePlacesCannotBeEmptyException();
    }
  }
}
