package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

import java.math.BigDecimal;

class EntriesAndWithdrawalAmountsAreNotEqualException extends BusinessException {
  EntriesAndWithdrawalAmountsAreNotEqualException(final BigDecimal deviation) {
    super("Entries and withdrawal amounts are not equal. Deviation is %s".formatted(deviation));
  }
}
