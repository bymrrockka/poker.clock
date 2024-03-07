package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.VALIDATION_ERROR;

public class NoStackException extends BusinessException {

  public NoStackException() {
    super("No stack specified.", VALIDATION_ERROR);
  }
}
