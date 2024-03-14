package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import java.math.BigDecimal;

import static by.mrrockka.service.exception.TelegramErrorCodes.ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL;

public class EntriesAndWithdrawalAmountsAreNotEqualException extends BusinessException {
  public EntriesAndWithdrawalAmountsAreNotEqualException(final BigDecimal total) {
    super("Entries and withdrawal amounts are not equal. Deviation is %s".formatted(total),
          ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL);
  }
}
