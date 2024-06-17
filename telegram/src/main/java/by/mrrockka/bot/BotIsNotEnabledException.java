package by.mrrockka.bot;

import by.mrrockka.exception.BusinessException;

class BotIsNotEnabledException extends BusinessException {
  BotIsNotEnabledException() {
    super("Bot is not enabled.");
  }
}
