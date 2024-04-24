package by.mrrockka.validation.prizepool;

import by.mrrockka.exception.BusinessException;

class PrizePoolPositionsHasGapsException extends BusinessException {
  PrizePoolPositionsHasGapsException(final int position) {
    super("Position %s is missed".formatted(position));
  }
}
