package by.mrrockka.domain.summary.finale;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.CANT_FIND_POSITION_IN_FINALE_PLACES;

class NoPrizeForPositionException extends BusinessException {

  NoPrizeForPositionException(final int position) {
    super("Can't find %s position in finale places.".formatted(position), CANT_FIND_POSITION_IN_FINALE_PLACES);
  }
}
