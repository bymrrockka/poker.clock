package by.mrrockka.validation.withdrawals;

import by.mrrockka.exception.BusinessException;

import java.math.BigDecimal;

class InsufficientEntriesAmountException extends BusinessException {

  InsufficientEntriesAmountException() {
    super("All chips were already withdrawn from a game");
  }

  InsufficientEntriesAmountException(final BigDecimal entriesAmount, final BigDecimal withdrawalsAmount) {
    super("Not enough entries to do a withdrawal. Total entries amount %s, total withdrawals amount %s"
            .formatted(entriesAmount, withdrawalsAmount));
  }
}
