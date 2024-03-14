package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import by.mrrockka.repo.game.GameType;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.PROCESSING_RESTRICTED;

public class ProcessingRestrictedException extends BusinessException {
  public ProcessingRestrictedException(@NonNull final GameType gameType) {
    super("Command processing restricted due to game type is not %s.".formatted(gameType), PROCESSING_RESTRICTED);
  }

  public ProcessingRestrictedException(@NonNull final String types) {
    super("Command processing restricted due to game type is not %s.".formatted(types), PROCESSING_RESTRICTED);
  }
}
