package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL;

public class EntriesAndWithdrawalAmountsAreNotEqualException extends BusinessException {
  public EntriesAndWithdrawalAmountsAreNotEqualException() {
    super("Entries and withdrawal amounts are not equal.", ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL);
  }
}
