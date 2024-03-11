package by.mrrockka.repo.withdrawals;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WithdrawalColumnNames {
  static final String GAME_ID = "game_id";
  static final String PERSON_ID = "person_id";
  static final String AMOUNT = "amount";
  static final String CREATED_AT = "created_at";
}
