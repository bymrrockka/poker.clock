package by.mrrockka.domain.summary.finale;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
class NoPrizeForPositionException extends BusinessException {

  NoPrizeForPositionException(final int position) {
    super("Can't find %s position in finale places.".formatted(position));
  }
}
