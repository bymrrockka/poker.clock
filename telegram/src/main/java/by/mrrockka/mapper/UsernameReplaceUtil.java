package by.mrrockka.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UsernameReplaceUtil {

  private static final String USERNAME_REPLACE_REGEX = "@me([\n \t\r\b]*)";
  private static final String BOT_NAME_REPLACE_REGEX = "@pokerclc_bot";

  public static String replaceUsername(final Message message) {
    return message.getText()
      .replaceFirst(USERNAME_REPLACE_REGEX, "@%s$1".formatted(message.getFrom().getUserName()))
      .replaceAll(BOT_NAME_REPLACE_REGEX, "");
  }
}
