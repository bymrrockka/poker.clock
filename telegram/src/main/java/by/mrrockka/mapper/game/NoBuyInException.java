package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.VALIDATION_ERROR;

public class NoBuyInException extends BusinessException {

  public NoBuyInException(final String regex) {
    super("No buy-in specified. Format %s".formatted(regex), VALIDATION_ERROR);
  }
}
