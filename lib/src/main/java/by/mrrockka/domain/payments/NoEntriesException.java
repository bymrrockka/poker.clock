package by.mrrockka.domain.payments;

import by.mrrockka.exception.BusinessException;
import by.mrrockka.exception.ErrorCodes;

public class NoEntriesException extends BusinessException {

  public NoEntriesException() {
    super("No entries for player", ErrorCodes.ENTRIES_NOT_FOUND);
  }
}
