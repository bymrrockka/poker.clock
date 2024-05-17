package by.mrrockka.domain.summary.player;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class PersonsNotMatchingException extends BusinessException {
  PersonsNotMatchingException(@NonNull final String person1, @NonNull final String person2) {
    super("Persons not mathing. %s against %s".formatted(person1, person2));
  }
}
