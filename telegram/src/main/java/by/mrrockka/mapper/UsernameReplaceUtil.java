package by.mrrockka.mapper;

import org.telegram.telegrambots.meta.api.objects.Message;

public class UsernameReplaceUtil {

  public static String replaceUsername(Message message) {
    return message.getText()
      .replaceFirst("@me([\n \t\r\b]*)", "@%s$1".formatted(message.getFrom().getUserName()));
  }
}
