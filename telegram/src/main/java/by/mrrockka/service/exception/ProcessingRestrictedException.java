package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import by.mrrockka.repo.game.GameType;
import lombok.NonNull;

public class ProcessingRestrictedException extends BusinessException {
  public ProcessingRestrictedException(@NonNull final GameType gameType) {
    this("Command processing restricted due to game type is not %s.".formatted(gameType));
  }

  public ProcessingRestrictedException(@NonNull final String message) {
    super(message);
  }

}
