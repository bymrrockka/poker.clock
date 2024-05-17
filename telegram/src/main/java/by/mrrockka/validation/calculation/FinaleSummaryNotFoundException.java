package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

class FinaleSummaryNotFoundException extends BusinessException {
  FinaleSummaryNotFoundException() {
    super("No finale places or prize pool specified, can't calculate finale summary.");
  }
}
