package by.mrrockka.validation.bounty;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

class PersonsCantBeEqualForBountyException extends BusinessException {
  PersonsCantBeEqualForBountyException(@NonNull final String nickname) {
    super("Same %s nickname for bounty transaction.".formatted(nickname));
  }
}
