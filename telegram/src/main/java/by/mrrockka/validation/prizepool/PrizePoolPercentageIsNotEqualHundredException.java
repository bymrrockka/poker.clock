package by.mrrockka.validation.prizepool;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
class PrizePoolPercentageIsNotEqualHundredException extends BusinessException {
  PrizePoolPercentageIsNotEqualHundredException() {
    super("Percentage for positions is not 100%");
  }
}
