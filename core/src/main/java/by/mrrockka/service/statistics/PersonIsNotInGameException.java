package by.mrrockka.service.statistics;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class PersonIsNotInGameException extends BusinessException {
  PersonIsNotInGameException(@NonNull String nickname) {
    super("Person @%s is not in game.".formatted(nickname));
  }
}
