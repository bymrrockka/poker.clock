package by.mrrockka.mapper.game;

import by.mrrockka.exception.BusinessException;

public class GameFieldIsNotSpecifiedException extends BusinessException {
  private static final String EXCEPTION_MESSAGE = "%s: number";
  private static final String BUY_IN = "buyin";
  private static final String STACK = "stack";
  private static final String BOUNTY = "bounty";

  public GameFieldIsNotSpecifiedException(final String field, final String message) {
    super("Game field %s is not specified. Format %s".formatted(field, message));
  }

  public static GameFieldIsNotSpecifiedException stack() {
    return new GameFieldIsNotSpecifiedException(STACK, EXCEPTION_MESSAGE.formatted(STACK));
  }

  public static GameFieldIsNotSpecifiedException buyin() {
    return new GameFieldIsNotSpecifiedException(BUY_IN, EXCEPTION_MESSAGE.formatted(BUY_IN));
  }

  public static GameFieldIsNotSpecifiedException bounty() {
    return new GameFieldIsNotSpecifiedException(BOUNTY, EXCEPTION_MESSAGE.formatted(BOUNTY));
  }
}
