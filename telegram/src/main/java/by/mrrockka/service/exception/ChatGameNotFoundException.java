package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;

import static by.mrrockka.service.exception.TelegramErrorCodes.CHAT_GAME_NOT_FOUND;

public class ChatGameNotFoundException extends BusinessException {
  public ChatGameNotFoundException() {
    super("Game is not found for this chat", CHAT_GAME_NOT_FOUND);
  }
}
