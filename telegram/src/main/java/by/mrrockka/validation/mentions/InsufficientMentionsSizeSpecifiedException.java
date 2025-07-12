package by.mrrockka.validation.mentions;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
public class InsufficientMentionsSizeSpecifiedException extends BusinessException {

  public InsufficientMentionsSizeSpecifiedException(final int has, final int required) {
    super("Not enough mentions. Message has %s but required %s".formatted(has, required));
  }
}
