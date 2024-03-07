package by.mrrockka.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UsernameReplaceUtil {

  public static String replaceUsername(final Message message) {
    return message.getText()
      .replaceFirst("@me([\n \t\r\b]*)", "@%s$1".formatted(message.getFrom().getUserName()));
  }
}
