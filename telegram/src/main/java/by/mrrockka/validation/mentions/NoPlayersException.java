package by.mrrockka.validation.mentions;

import by.mrrockka.exception.BusinessException;

public class NoPlayersException extends BusinessException {

  public NoPlayersException() {
    super("Can't start a game with no players.");
  }
}
