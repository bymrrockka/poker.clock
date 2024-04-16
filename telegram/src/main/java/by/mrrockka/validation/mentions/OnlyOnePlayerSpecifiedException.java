package by.mrrockka.validation.mentions;

import by.mrrockka.exception.BusinessException;

public class OnlyOnePlayerSpecifiedException extends BusinessException {

  public OnlyOnePlayerSpecifiedException() {
    super("Only one player specified.");
  }
}