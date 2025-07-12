package by.mrrockka.aspect;

import by.mrrockka.exception.BusinessException;

@Deprecated(forRemoval = true)
public class ChatIdNotFoundException extends BusinessException {
  public ChatIdNotFoundException() {
    super("Updates don't have chat id");
  }
}
