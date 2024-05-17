package by.mrrockka.repo;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

//todo: verify if needed
public class NullableResultSetException extends BusinessException {

  public NullableResultSetException(@NonNull final String message) {
    super(message);
  }

  public NullableResultSetException() {
    this("Don't have related data in db");
  }
}
