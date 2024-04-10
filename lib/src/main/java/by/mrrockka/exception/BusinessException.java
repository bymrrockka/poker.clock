package by.mrrockka.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BusinessException extends RuntimeException {

  @NonNull
  String message;
  String code;
  String humanReadableCode;

  public BusinessException(@NonNull final String message) {
    this(message, null);
  }

  public BusinessException(@NonNull final String message, final String humanReadableCode) {
    this(message, null, humanReadableCode);
  }

  public BusinessException(@NonNull final String message, final String code, final String humanReadableCode) {
    super(message);
    this.message = message;
    this.code = code;
    this.humanReadableCode = humanReadableCode;
  }

  @Override
  public String toString() {
    final var strBuilder = new StringBuilder();

    strBuilder.append("ERROR\n");
    strBuilder.append("Message: %s\n".formatted(message));
    if (StringUtils.isNoneBlank(humanReadableCode)) {
      strBuilder.append("Readable code: %s\n".formatted(humanReadableCode));
    }
    if (StringUtils.isNoneBlank(code)) {
      strBuilder.append("Code: %s\n".formatted(code));
    }

    return strBuilder.toString();
  }
}
