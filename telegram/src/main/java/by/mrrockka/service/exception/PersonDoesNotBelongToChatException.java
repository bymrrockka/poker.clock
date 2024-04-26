package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

public class PersonDoesNotBelongToChatException extends BusinessException {
  public PersonDoesNotBelongToChatException(@NonNull final String nickname) {
    super("Person @%s does not belong to chat".formatted(nickname));
  }
}
