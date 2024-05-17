package by.mrrockka.bot;

import by.mrrockka.exception.BusinessException;

class NoRoutesFoundException extends BusinessException {
  NoRoutesFoundException() {
    super("No route found");
  }
}
