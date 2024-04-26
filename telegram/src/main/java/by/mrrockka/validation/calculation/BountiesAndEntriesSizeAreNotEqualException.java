package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.BOUNTIES_AND_ENTRIES_SIZE_ARE_NOT_EQUAL;

class BountiesAndEntriesSizeAreNotEqualException extends BusinessException {
  BountiesAndEntriesSizeAreNotEqualException(final int deviation) {
    super("Bounties and entries size are not equal. Deviation %s".formatted(deviation),
          BOUNTIES_AND_ENTRIES_SIZE_ARE_NOT_EQUAL);
  }
}
