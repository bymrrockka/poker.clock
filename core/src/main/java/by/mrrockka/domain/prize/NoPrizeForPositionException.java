package by.mrrockka.domain.prize;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
class NoPrizeForPositionException extends BusinessException {
  NoPrizeForPositionException(final int position) {
    super("No percentage found for %d position".formatted(position));
  }
}
