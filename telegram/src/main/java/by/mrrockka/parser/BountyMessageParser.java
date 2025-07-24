package by.mrrockka.parser;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.mapper.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static by.mrrockka.parser.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class BountyMessageParser {

  private final TelegramPersonMapper personMapper;

  private static final String BOUNTY_REGEX = "^/bounty([ ]+)%s([ ]+)kicked([ ]+)%s$".formatted(
    TELEGRAM_NAME_REGEX, TELEGRAM_NAME_REGEX);

  private static final int FROM_GROUP = 5;
  private static final int TO_GROUP = 2;

  private static final String ERROR_MESSAGE = "/bounty @nickname kicked @nickname";

  public Pair<String, String> parse(final MessageMetadata messageMetadata) {
    final var str = messageMetadata.getText().toLowerCase().strip();
    final var mentions = messageMetadata.mentionsStream()
      .map(entity -> entity.text().replaceAll("@", ""))
      .toList();
    final var matcher = Pattern.compile(BOUNTY_REGEX).matcher(str);

    if (!matcher.matches()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    final var from = mentions.stream()
      .filter(mention -> mention.equalsIgnoreCase(matcher.group(FROM_GROUP)))
      .findAny()
      .orElseThrow();

    final var to = mentions.stream()
      .filter(mention -> mention.equalsIgnoreCase(matcher.group(TO_GROUP)))
      .findAny()
      .orElseThrow();

    return Pair.of(from, to);
  }

}
