package by.mrrockka.domain.summary;

import by.mrrockka.domain.Person;
import by.mrrockka.exception.BusinessException;

import static by.mrrockka.exception.ErrorCodes.CANT_FIND_PERSON_IN_FINALE_PLACES;

class NoPrizeForPersonException extends BusinessException {

  NoPrizeForPersonException(final Person person) {
    super("Can't find %s person in finale places.".formatted(person.getNickname()), CANT_FIND_PERSON_IN_FINALE_PLACES);
  }
}
