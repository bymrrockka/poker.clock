package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

@Deprecated(forRemoval = true)
public class EntriesForPersonNotFoundException extends BusinessException {
  public EntriesForPersonNotFoundException() {
    super("Entries for person not found in game.");
  }

  public EntriesForPersonNotFoundException(@NonNull final String nickname) {
    super("Entries for person with %s not found in game".formatted(nickname));
  }
}
