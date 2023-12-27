package by.mrrockka.model.payments;

public class NoPaymentsException extends RuntimeException {

  public NoPaymentsException() {
    super("No active payments for player");
  }
}
