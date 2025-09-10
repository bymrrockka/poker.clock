package by.mrrockka.domain.collection;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
public class NoEntriesException extends BusinessException {

  public NoEntriesException() {
    super("No entries for player");
  }
}
