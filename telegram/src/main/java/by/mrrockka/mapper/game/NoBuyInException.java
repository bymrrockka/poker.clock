package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

public class NoBuyInException extends BusinessException {

  public NoBuyInException() {
    super("No buy-in specified.");
  }
}
