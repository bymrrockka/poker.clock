package by.mrrockka.bot.command;

import by.mrrockka.exception.BusinessException;

public class CommandProcessingIsDisabledException extends BusinessException {
  public CommandProcessingIsDisabledException() {
    super("Command processing is disabled.");
  }
}
