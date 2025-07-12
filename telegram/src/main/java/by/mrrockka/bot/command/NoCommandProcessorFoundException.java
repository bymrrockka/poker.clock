package by.mrrockka.bot.command;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

@Deprecated(forRemoval = true)
class NoCommandProcessorFoundException extends BusinessException {
  NoCommandProcessorFoundException() {
    super("No command processors found.");
  }

  NoCommandProcessorFoundException(@NonNull final String command) {
    super("No command processors found for %s command.".formatted(command));
  }
}
