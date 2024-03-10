package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.FINAL_PLACE_CONTAINS_TELEGRAM_OF_NON_EXISTING_PLAYER;

public class FinalPlaceContainsTelegramOfNotExistingPlayerException extends BusinessException {
  public FinalPlaceContainsTelegramOfNotExistingPlayerException(@NonNull final String telegram) {
    super("Final place contains telegram %s of non existing player.".formatted(telegram),
          FINAL_PLACE_CONTAINS_TELEGRAM_OF_NON_EXISTING_PLAYER
    );
  }
}
