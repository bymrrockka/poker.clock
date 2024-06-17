package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

class BountiesAndEntriesSizeAreNotEqualException extends BusinessException {
  BountiesAndEntriesSizeAreNotEqualException(final int deviation) {
    super("Bounties and entries size are not equal. Deviation %s".formatted(deviation));
  }
}
