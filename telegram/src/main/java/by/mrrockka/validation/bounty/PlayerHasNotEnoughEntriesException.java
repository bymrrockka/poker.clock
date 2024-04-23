package by.mrrockka.validation.bounty;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.NOT_ENOUGH_ENTRIES_FOR_BOUNTY_TRANSACTION;

class PlayerHasNotEnoughEntriesException extends BusinessException {
  PlayerHasNotEnoughEntriesException(@NonNull final String telegram) {
    super("%s has not enough entries.".formatted(telegram), NOT_ENOUGH_ENTRIES_FOR_BOUNTY_TRANSACTION);
  }
}
