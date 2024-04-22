package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

import java.math.BigDecimal;

import static by.mrrockka.service.exception.TelegramErrorCodes.ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL;

class EntriesAndWithdrawalAmountsAreNotEqualException extends BusinessException {
  EntriesAndWithdrawalAmountsAreNotEqualException(final BigDecimal total) {
    super("Entries and withdrawal amounts are not equal. Deviation is %s".formatted(total),
          ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL);
  }
}
