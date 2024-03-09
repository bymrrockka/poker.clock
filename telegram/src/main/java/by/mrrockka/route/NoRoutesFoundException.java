package by.mrrockka.route;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.ROUTE_NOT_FOUND;

class NoRoutesFoundException extends BusinessException {
  public NoRoutesFoundException(final String message) {
    super("Message: No routes found for \"%s\"".formatted(message), ROUTE_NOT_FOUND);
  }
}
