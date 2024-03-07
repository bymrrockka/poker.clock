package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import java.util.UUID;

import static by.mrrockka.service.exception.TelegramErrorCodes.PERSON_HAS_NO_TELEGRAM;

@Deprecated(forRemoval = true, since = "1.0.1")
public class PersonHasNoTelegramException extends BusinessException {
  public PersonHasNoTelegramException(final UUID personId) {
    super("Person %s does not have telegram".formatted(personId), PERSON_HAS_NO_TELEGRAM);
  }
}
