package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.BOUNTIES_AND_ENTRIES_SIZE_ARE_NOT_EQUAL;

public class BountiesAndEntriesSizeAreNotEqualException extends BusinessException {
  public BountiesAndEntriesSizeAreNotEqualException(@NonNull final int deviation) {
    super("Bounties and entries size are not equal. Deviation %s".formatted(deviation),
          BOUNTIES_AND_ENTRIES_SIZE_ARE_NOT_EQUAL);
  }
}
