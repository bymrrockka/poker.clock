package by.mrrockka.domain.summary.finale;

import by.mrrockka.exception.BusinessException;

class NoPrizeForPositionException extends BusinessException {

  NoPrizeForPositionException(final int position) {
    super("Can't find %s position in finale places.".formatted(position));
  }
}
