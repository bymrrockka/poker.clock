package by.mrrockka.domain.payments;

public class NoEntriesException extends RuntimeException {

  public NoEntriesException() {
    super("No entries for player");
  }
}
