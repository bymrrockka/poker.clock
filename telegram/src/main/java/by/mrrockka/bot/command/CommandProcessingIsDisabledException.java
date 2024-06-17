package by.mrrockka.bot.command;

import by.mrrockka.exception.BusinessException;

class CommandProcessingIsDisabledException extends BusinessException {
  CommandProcessingIsDisabledException() {
    super("Command processing is disabled.");
  }
}
