package by.mrrockka.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandRegexConstants {
  public static final String TELEGRAM_NAME_REGEX = "@([A-z0-9_-]{5,})";
  public static final String DELIMITER_REGEX = "([. :\\-=]{1,3})";
  public static final String ME_REGEX = "@me(([\n \t\r\\W]+)|$)";
  public static final String COMMAND_APPENDIX = "([\\W\\w]*)";
}
