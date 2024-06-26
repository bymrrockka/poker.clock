package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

public class ChatGameNotFoundException extends BusinessException {
  public ChatGameNotFoundException() {
    super("Game is not found for this chat");
  }
}
