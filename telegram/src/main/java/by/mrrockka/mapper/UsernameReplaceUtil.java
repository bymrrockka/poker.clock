package by.mrrockka.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import static by.mrrockka.mapper.CommandRegexConstants.BOT_NAME_REPLACE_REGEX;
import static by.mrrockka.mapper.CommandRegexConstants.USERNAME_REPLACE_REGEX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UsernameReplaceUtil {

  public static String replaceUsername(final Message message) {
    return message.getText()
      .replaceFirst(USERNAME_REPLACE_REGEX, "@%s$1".formatted(message.getFrom().getUserName()))
      .replaceAll(BOT_NAME_REPLACE_REGEX, "");
  }
}
