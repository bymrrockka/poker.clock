package by.mrrockka.validation.prizepool;

import by.mrrockka.exception.BusinessException;

class PrizePoolPercentageIsNotEqualHundredException extends BusinessException {
  PrizePoolPercentageIsNotEqualHundredException() {
    super("Percentage for positions is not 100%");
  }
}
