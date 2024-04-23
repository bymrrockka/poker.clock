package by.mrrockka.validation.finaleplaces;

import by.mrrockka.exception.BusinessException;

public class FinalePlacesCannotBeEmptyException extends BusinessException {
  public FinalePlacesCannotBeEmptyException() {
    super("Finale places can't be empty.");
  }
}
