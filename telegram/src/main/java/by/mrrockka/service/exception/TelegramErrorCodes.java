package by.mrrockka.service.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TelegramErrorCodes {

  public static final String CHAT_GAME_NOT_FOUND = "chat.game.not.found";
  public static final String CANT_CALCULATE_FINALE_SUMMARY = "cant.calculate.finale.summary";
  public static final String INVALID_MESSAGE_FORMAT = "invalid.message.format";
  public static final String PAYOUTS_ARE_NOT_CALCULATED = "payouts.are.not.calculated";
  public static final String FINAL_PLACE_CONTAINS_TELEGRAM_OF_NON_EXISTING_PLAYER = "invalid.final.place";
  public static final String MODEL_IS_EMPTY = "model.is.empty";
  public static final String ROUTE_NOT_FOUND = "route.not.found";
  public static final String CHAT_ID_NOT_FOUND = "chat.id.not.found";
  public static final String PROCESSING_RESTRICTED = "processing.restricted";
  public static final String PERSONS_CANT_BE_EQUAL_FOR_BOUNTY = "persons.cant.be.equal.for.bounty";
  public static final String ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL = "entries.and.withdrawal.amounts.are.not.equal";
  public static final String NOT_ENOUGH_ENTRIES_FOR_BOUNTY_TRANSACTION = "not.enough.entries.for.bounty.transaction";
  public static final String BOUNTIES_AND_ENTRIES_SIZE_ARE_NOT_EQUAL = "bounties.and.entries.size.are.not.equal";
}
