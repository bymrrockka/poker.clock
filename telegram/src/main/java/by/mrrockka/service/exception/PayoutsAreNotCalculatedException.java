package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

public class PayoutsAreNotCalculatedException extends BusinessException {
  public PayoutsAreNotCalculatedException() {
    super("Payouts are not calculated.");
  }
}
