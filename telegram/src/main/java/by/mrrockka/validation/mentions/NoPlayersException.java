package by.mrrockka.validation.mentions;

import by.mrrockka.exception.BusinessException;

//Use InsufficientMentionsSizeSpecifiedException instead
@Deprecated(since = "1.0.0", forRemoval = true)
public class NoPlayersException extends BusinessException {

  public NoPlayersException() {
    super("Can't start a game with no players.");
  }
}
