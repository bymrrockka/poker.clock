package by.mrrockka.service.exception;

import by.mrrockka.domain.game.GameType;
import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.PROCESSING_RESTRICTED;

public class ProcessingRestrictedException extends BusinessException {
  public ProcessingRestrictedException(@NonNull final GameType gameType) {
    super("Command processing restricted due to game type is not %s.".formatted(gameType), PROCESSING_RESTRICTED);
  }
}
