package by.mrrockka.domain.summary.player;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.exception.ErrorCodes.PERSONS_NOT_MATCHING;

public class PersonsNotMatchingException extends BusinessException {
  public PersonsNotMatchingException(@NonNull final String person1, @NonNull final String person2) {
    super("Persons not mathing. %s against %s".formatted(person1, person2), PERSONS_NOT_MATCHING);
  }
}
