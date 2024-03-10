package by.mrrockka.mapper.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.INVALID_MESSAGE_FORMAT;

public class InvalidMessageFormatException extends BusinessException {
  public InvalidMessageFormatException(@NonNull final String regex) {
    super("Message is not applicable. Format of the message is %s".formatted(regex), INVALID_MESSAGE_FORMAT);
  }
}
