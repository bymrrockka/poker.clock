package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.NOT_ENOUGH_ENTRIES_FOR_BOUNTY_TRANSACTION;

public class PlayerHasNoEnoughEntriesException extends BusinessException {
  public PlayerHasNoEnoughEntriesException(@NonNull final String telegram) {
    super("%s has not enough entries.".formatted(telegram), NOT_ENOUGH_ENTRIES_FOR_BOUNTY_TRANSACTION);
  }
}
