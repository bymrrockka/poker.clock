package by.mrrockka.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BusinessException extends RuntimeException {

  @NonNull
  String message;
  String code;
  String humanReadableCode;

  public BusinessException(@NonNull final String message, final String humanReadableCode) {
    super(message);
    this.message = message;
    this.humanReadableCode = humanReadableCode;
    this.code = null;
  }

  public BusinessException(@NonNull final String message, final String code, final String humanReadableCode) {
    super(message);
    this.message = message;
    this.code = code;
    this.humanReadableCode = humanReadableCode;
  }
}
