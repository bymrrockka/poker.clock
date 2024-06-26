package by.mrrockka.validation.mentions;

import by.mrrockka.exception.BusinessException;

public class PlayerHasNoNicknameException extends BusinessException {

  public PlayerHasNoNicknameException() {
    this("who posted message");
  }

  public PlayerHasNoNicknameException(final String text) {
    super("Player %s has no nickname.".formatted(text));
  }
}
