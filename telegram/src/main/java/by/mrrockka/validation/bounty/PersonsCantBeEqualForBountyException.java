package by.mrrockka.validation.bounty;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.PERSONS_CANT_BE_EQUAL_FOR_BOUNTY;

class PersonsCantBeEqualForBountyException extends BusinessException {
  PersonsCantBeEqualForBountyException(@NonNull final String nickname) {
    super("Same %s nickname for bounty transaction.".formatted(nickname), PERSONS_CANT_BE_EQUAL_FOR_BOUNTY);
  }
}
