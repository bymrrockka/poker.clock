package by.mrrockka.bot;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.ROUTE_NOT_FOUND;

class NoRoutesFoundException extends BusinessException {
  NoRoutesFoundException() {
    super("No route found", ROUTE_NOT_FOUND);
  }
}
