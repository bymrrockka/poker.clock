package by.mrrockka.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import static by.mrrockka.mapper.CommandRegexConstants.COMMAND_APPENDIX;
import static by.mrrockka.mapper.CommandRegexConstants.ME_REGEX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MeMentionUtil {

  public static String replaceMeMention(final Message message) {
    return message.getText()
      .replaceFirst(ME_REGEX, "@%s$1".formatted(message.getFrom().getUserName()));
  }

  public static boolean hasMeMention(final Message message) {
    return message.getText().matches("%s%s%s".formatted(COMMAND_APPENDIX, ME_REGEX, COMMAND_APPENDIX));
  }
}
