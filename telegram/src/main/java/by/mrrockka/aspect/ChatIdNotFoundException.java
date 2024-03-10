package by.mrrockka.aspect;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.CHAT_ID_NOT_FOUND;

public class ChatIdNotFoundException extends BusinessException {
  public ChatIdNotFoundException() {
    super("Updates don't have chat id", CHAT_ID_NOT_FOUND);
  }
}
