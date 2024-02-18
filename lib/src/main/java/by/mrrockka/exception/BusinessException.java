package by.mrrockka.exception;

//todo: inherit all exception from this
public abstract class BusinessException extends RuntimeException {

  public BusinessException(String message) {
    super(message);
  }
}
