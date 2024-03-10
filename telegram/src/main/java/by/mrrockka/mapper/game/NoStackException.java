package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.VALIDATION_ERROR;

public class NoStackException extends BusinessException {

  public NoStackException(final String regex) {
    super("No stack specified. Format %s".formatted(regex), VALIDATION_ERROR);
  }
}
