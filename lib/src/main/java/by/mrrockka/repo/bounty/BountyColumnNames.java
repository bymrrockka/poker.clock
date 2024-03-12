package by.mrrockka.repo.bounty;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BountyColumnNames {
  static final String GAME_ID = "game_id";
  static final String FROM_PERSON = "from_person";
  static final String TO_PERSON = "to_person";
  static final String AMOUNT = "amount";
  static final String CREATED_AT = "created_at";
}
