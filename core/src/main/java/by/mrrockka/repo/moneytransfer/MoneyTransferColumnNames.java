package by.mrrockka.repo.moneytransfer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoneyTransferColumnNames {
  public static final String GAME_ID = "game_id";
  public static final String PERSON_ID = "person_id";
  public static final String AMOUNT = "amount";
  public static final String TYPE = "type";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
}
