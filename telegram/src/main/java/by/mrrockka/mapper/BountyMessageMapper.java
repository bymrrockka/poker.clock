package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
public class BountyMessageMapper {

  private static final String BOUNTY_REGEX = "^/bounty([ ]+)%s([ ]+)kicked([ ]+)%s$".formatted(
    TELEGRAM_NAME_REGEX, TELEGRAM_NAME_REGEX);

  public Pair<String, String> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(BOUNTY_REGEX).matcher(str);
    if (matcher.matches()) {
      return Pair.of(matcher.group(2), matcher.group(5));
    }

    throw new InvalidMessageFormatException(BOUNTY_REGEX);
  }
}
