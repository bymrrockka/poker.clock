package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.CANT_CALCULATE_GAME_SUMMARY;

public class GameSummaryNotFoundException extends BusinessException {
  public GameSummaryNotFoundException() {
    super("No finale places or prize pool specified, can't calculate game summary.", CANT_CALCULATE_GAME_SUMMARY);
  }
}
