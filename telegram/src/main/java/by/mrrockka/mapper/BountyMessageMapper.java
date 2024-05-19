package by.mrrockka.mapper;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class BountyMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.nickname}")
  @Setter
  private String botName;

  private static final String BOUNTY_REGEX = "^/bounty([ ]+)%s([ ]+)kicked([ ]+)%s$".formatted(
    TELEGRAM_NAME_REGEX, TELEGRAM_NAME_REGEX);

  private static final int FROM_GROUP = 5;
  private static final int TO_GROUP = 2;

  private static final String ERROR_MESSAGE = "/bounty @nickname kicked @nickname";

  @Deprecated(since = "1.1.0", forRemoval = true)
  public Pair<String, String> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(BOUNTY_REGEX).matcher(str);
    if (matcher.matches()) {
      return Pair.of(matcher.group(FROM_GROUP), matcher.group(TO_GROUP));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

  public Pair<TelegramPerson, TelegramPerson> map(final MessageMetadata messageMetadata) {
    final var chatId = messageMetadata.chatId();
    final var str = messageMetadata.text().toLowerCase().strip();
    final var mentions = messageMetadata.mentions()
      .map(entity -> personMapper.mapMessageToTelegramPerson(entity, chatId))
      .toList();
    final var matcher = Pattern.compile(BOUNTY_REGEX).matcher(str);

    if (!matcher.matches()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    final var from = mentions.stream()
      .filter(mention -> mention.getNickname().equalsIgnoreCase(matcher.group(FROM_GROUP)))
      .findAny()
      .orElseThrow();

    final var to = mentions.stream()
      .filter(mention -> mention.getNickname().equalsIgnoreCase(matcher.group(TO_GROUP)))
      .findAny()
      .orElseThrow();

    return Pair.of(from, to);
  }

}
