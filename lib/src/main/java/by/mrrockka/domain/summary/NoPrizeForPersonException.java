package by.mrrockka.domain.summary;

import by.mrrockka.domain.player.Person;

class NoPrizeForPersonException extends RuntimeException {
  NoPrizeForPersonException(Person person) {
    super("No prize found for %s person".formatted(person.lastName()));
  }
}
