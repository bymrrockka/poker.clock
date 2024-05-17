package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

public class FinalPlaceContainsNicknameOfNonExistingPlayerException extends BusinessException {
  public FinalPlaceContainsNicknameOfNonExistingPlayerException(@NonNull final String nickname) {
    super("Final place contains nickname %s of non existing player.".formatted(nickname));
  }
}
