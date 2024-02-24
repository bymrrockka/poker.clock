package by.mrrockka.mapper.person;

import by.mrrockka.exception.BusinessException;

public class NoPlayersException extends BusinessException {

  public NoPlayersException() {
    super("Players list could not be empty or contain only one player.");
  }
}