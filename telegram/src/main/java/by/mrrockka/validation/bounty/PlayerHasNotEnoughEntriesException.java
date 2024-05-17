package by.mrrockka.validation.bounty;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class PlayerHasNotEnoughEntriesException extends BusinessException {
  PlayerHasNotEnoughEntriesException(@NonNull final String nickname) {
    super("%s has not enough entries.".formatted(nickname));
  }
}
