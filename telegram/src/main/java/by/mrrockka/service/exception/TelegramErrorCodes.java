package by.mrrockka.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TelegramErrorCodes {

  public static final String CHAT_GAME_NOT_FOUND = "chat.game.not.found";
  public static final String CANT_CALCULATE_GAME_SUMMARY = "cant.calculate.game.summary";
  public static final String INVALID_MESSAGE_FORMAT = "invalid.message.format";
  public static final String PAYOUTS_ARE_NOT_CALCULATED = "payouts.are.not.calculated";

  //todo: remove when PersonHasNoTelegramException.class will be removed
  public static final String PERSON_HAS_NO_TELEGRAM = "person.has.no.telegram";
  public static final String FINAL_PLACE_CONTAINS_TELEGRAM_OF_NON_EXISTING_PLAYER = "invalid.final.place";
  public static final String MODEL_IS_EMPTY = "model.is.empty";
  public static final String ROUTE_NOT_FOUND = "route.not.found";
  public static final String CHAT_ID_NOT_FOUND = "chat.id.not.found";
}
