package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.CANT_CALCULATE_FINALE_SUMMARY;

public class FinaleSummaryNotFoundException extends BusinessException {
  public FinaleSummaryNotFoundException() {
    super("No finale places or prize pool specified, can't calculate finale summary.", CANT_CALCULATE_FINALE_SUMMARY);
  }
}
