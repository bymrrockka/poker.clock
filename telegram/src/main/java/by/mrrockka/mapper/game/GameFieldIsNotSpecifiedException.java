package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

public class GameFieldIsNotSpecifiedException extends BusinessException {

  public GameFieldIsNotSpecifiedException(final String field, final String regex) {
    super("Game field %s is not specified. Format %s".formatted(field, regex));
  }

//  todo: add readable exception message instead of regex

  public static GameFieldIsNotSpecifiedException stack(final String regex) {
    return new GameFieldIsNotSpecifiedException("stack", regex);
  }

  public static GameFieldIsNotSpecifiedException buyin(final String regex) {
    return new GameFieldIsNotSpecifiedException("buyin", regex);
  }

  public static GameFieldIsNotSpecifiedException bounty(final String regex) {
    return new GameFieldIsNotSpecifiedException("bounty", regex);
  }
}
