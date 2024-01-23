package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

public class NoStackException extends BusinessException {

  public NoStackException() {
    super("No stack specified.");
  }
}
