package by.mrrockka.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorCodes {

  public static final String VALIDATION_ERROR = "validation.error";
  public static final String ENTRIES_NOT_FOUND = "not.found.entries";
  public static final String ENTRIES_AND_WITHDRAWAL_AMOUNTS_ARE_NOT_EQUAL = "entries.and.withdrawal.amounts.are.not.equal";
  public static final String PRIZE_FOR_POSITION_NOT_FOUND = "not.found.prize.for.position";
  public static final String CANT_FIND_PERSON_IN_FINALE_PLACES = "cant.find.person.in.finale.places";
  public static final String QUERY_EXECUTION_RETURNS_NOTHING = "query.execution.returns.nothing";
  public static final String PERSONS_NOT_MATCHING = "persons.not.matching";
}
