package by.mrrockka.bot.command;

import by.mrrockka.exception.BusinessException;

class NoCommandProcessorFoundException extends BusinessException {
  NoCommandProcessorFoundException() {
    super("No command processors found.");
  }
}
