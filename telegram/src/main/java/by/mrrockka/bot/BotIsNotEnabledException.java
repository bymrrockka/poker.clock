package by.mrrockka.bot;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
class BotIsNotEnabledException extends BusinessException {
  BotIsNotEnabledException() {
    super("Bot is not enabled.");
  }
}
