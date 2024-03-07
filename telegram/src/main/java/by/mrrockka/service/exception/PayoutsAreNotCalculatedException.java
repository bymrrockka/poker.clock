package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.PAYOUTS_ARE_NOT_CALCULATED;

public class PayoutsAreNotCalculatedException extends BusinessException {
  public PayoutsAreNotCalculatedException() {
    super("Payouts are not calculated.", PAYOUTS_ARE_NOT_CALCULATED);
  }
}
