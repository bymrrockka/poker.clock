package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.PERSONS_CANT_BE_EQUAL_FOR_BOUNTY;

public class PersonsCantBeEqualForBountyException extends BusinessException {
  public PersonsCantBeEqualForBountyException(@NonNull final String nickname) {
    super("Same %s nickname for bounty transaction.".formatted(nickname), PERSONS_CANT_BE_EQUAL_FOR_BOUNTY);
  }
}
