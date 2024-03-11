package by.mrrockka.domain.entries;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.ENTRIES_NOT_FOUND;

public class NoEntriesException extends BusinessException {

  public NoEntriesException() {
    super("No entries for player", ENTRIES_NOT_FOUND);
  }
}
