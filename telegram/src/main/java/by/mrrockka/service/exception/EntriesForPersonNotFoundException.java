package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.exception.ErrorCodes.ENTRIES_NOT_FOUND;

public class EntriesForPersonNotFoundException extends BusinessException {
  public EntriesForPersonNotFoundException() {
    super("Entries for person not found in game.", ENTRIES_NOT_FOUND);
  }

  public EntriesForPersonNotFoundException(@NonNull final String nickname) {
    super("Entries for person with %s not found in game".formatted(nickname), ENTRIES_NOT_FOUND);
  }
}
