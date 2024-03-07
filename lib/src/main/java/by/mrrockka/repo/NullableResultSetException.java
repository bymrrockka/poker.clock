package by.mrrockka.repo;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.exception.ErrorCodes.QUERY_EXECUTION_RETURNS_NOTHING;

public class NullableResultSetException extends BusinessException {

  public NullableResultSetException(@NonNull final String message) {
    super(message, QUERY_EXECUTION_RETURNS_NOTHING);
  }

  public NullableResultSetException() {
    this("Don't have related data in db");
  }
}
