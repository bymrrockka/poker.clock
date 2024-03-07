package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.VALIDATION_ERROR;

public class NoBuyInException extends BusinessException {

  public NoBuyInException() {
    super("No buy-in specified.", VALIDATION_ERROR);
  }
}
