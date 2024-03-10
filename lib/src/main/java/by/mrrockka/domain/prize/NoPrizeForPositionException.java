package by.mrrockka.domain.prize;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.PRIZE_FOR_POSITION_NOT_FOUND;

class NoPrizeForPositionException extends BusinessException {
  NoPrizeForPositionException(final int position) {
    super("No percentage found for %d position".formatted(position), PRIZE_FOR_POSITION_NOT_FOUND);
  }
}
