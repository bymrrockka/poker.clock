package by.mrrockka.parser.finaleplaces;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.TelegramPersonMapper;
import by.mrrockka.parser.InvalidMessageFormatException;
import by.mrrockka.service.exception.FinalPlaceContainsNicknameOfNonExistingPlayerException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.parser.CommandRegexConstants.DELIMITER_REGEX;
import static by.mrrockka.parser.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class FinalePlacesMessageParser {

  private final TelegramPersonMapper personMapper;

  private static final String FINALE_PLACES_REGEX = "^(\\d)%s%s$".formatted(DELIMITER_REGEX, TELEGRAM_NAME_REGEX);
  private static final int POSITION_GROUP = 1;
  private static final int MENTION_GROUP = 3;
  private static final String ERROR_MESSAGE = "/finaleplaces 1 @nickname (, #position @nickname)";

  public Map<Integer, TelegramPerson> parse(final MessageMetadata metadata) {
    final var strings = metadata.text()
      .toLowerCase()
      .strip()
      .replaceAll("/finaleplaces([ \n]*)", "")
      .split("[\n,;]");
    final var placesPattern = Pattern.compile(FINALE_PLACES_REGEX);
    final var finalePlaces = Arrays.stream(strings)
      .map(String::strip)
      .map(placesPattern::matcher)
      .filter(Matcher::matches)
      .toList();
    final var chatId = metadata.chatId();
    final var mentions = metadata.mentions()
      .map(entity -> personMapper.mapMessageToTelegramPerson(entity, chatId))
      .toList();

    if (finalePlaces.isEmpty()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    if (finalePlaces.size() != mentions.size()) {
      throw new FinalePlacesDoNotMatchMentionsSizeException();
    }

    return finalePlaces.stream()
      .map(matcher -> Pair.of(Integer.parseInt(matcher.group(POSITION_GROUP)),
                              findMentionByNickname(mentions, matcher.group(MENTION_GROUP))))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private TelegramPerson findMentionByNickname(final List<TelegramPerson> mentions, final String nickname) {
    return mentions.stream()
      .filter(person -> person.getNickname().equalsIgnoreCase(nickname))
      .findFirst()
      .orElseThrow(() -> new FinalPlaceContainsNicknameOfNonExistingPlayerException(nickname));
  }

}
