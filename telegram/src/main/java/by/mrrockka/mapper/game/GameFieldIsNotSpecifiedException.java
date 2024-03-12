package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.VALIDATION_ERROR;

public class GameFieldIsNotSpecifiedException extends BusinessException {

  public GameFieldIsNotSpecifiedException(final String field, final String regex) {
    super("Game field %s is not specified. Format %s".formatted(field, regex), VALIDATION_ERROR);
  }

  public static GameFieldIsNotSpecifiedException stack(final String regex) {
    return new GameFieldIsNotSpecifiedException("stack", regex);
  }

  public static GameFieldIsNotSpecifiedException buyin(final String regex) {
    return new GameFieldIsNotSpecifiedException("buy-in", regex);
  }

  public static GameFieldIsNotSpecifiedException bounty(final String regex) {
    return new GameFieldIsNotSpecifiedException("bounty", regex);
  }
}
