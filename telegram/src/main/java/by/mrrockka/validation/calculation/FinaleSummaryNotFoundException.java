package by.mrrockka.validation.calculation;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.CANT_CALCULATE_FINALE_SUMMARY;

class FinaleSummaryNotFoundException extends BusinessException {
  FinaleSummaryNotFoundException() {
    super("No finale places or prize pool specified, can't calculate finale summary.", CANT_CALCULATE_FINALE_SUMMARY);
  }
}
