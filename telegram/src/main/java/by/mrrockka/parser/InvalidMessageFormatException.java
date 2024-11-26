package by.mrrockka.parser;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

public class InvalidMessageFormatException extends BusinessException {
  public InvalidMessageFormatException(@NonNull final String regex) {
    super("Can't read message. Should be like \"%s\"".formatted(regex));
  }
}
