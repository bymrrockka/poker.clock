package by.mrrockka.mapper.finaleplaces;

import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import by.mrrockka.service.exception.FinalPlaceContainsNicknameOfNonExistingPlayerException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.mapper.CommandRegexConstants.DELIMITER_REGEX;
import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class FinalePlacesMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.nickname}")
  @Setter
  private String botName;

  private static final String FINALE_PLACES_REGEX = "^(\\d)%s%s$".formatted(DELIMITER_REGEX, TELEGRAM_NAME_REGEX);
  private static final int POSITION_GROUP = 1;
  private static final int MENTION_GROUP = 3;
  private static final String ERROR_MESSAGE = "/finaleplaces 1 @nickname (, #position @nickname)";

  @Deprecated(since = "1.1.0", forRemoval = true)
  public List<Pair<Integer, String>> map(final String command) {
    final var strings = command.toLowerCase().strip().replaceAll("/finaleplaces([ \n]*)", "").split("[\n,;]");
    final var placesPattern = Pattern.compile(FINALE_PLACES_REGEX);
    final var result = Arrays.stream(strings)
      .map(String::strip)
      .map(placesPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> Pair.of(Integer.parseInt(matcher.group(POSITION_GROUP)), matcher.group(MENTION_GROUP)))
      .collect(Collectors.toList());

    if (result.isEmpty()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    return result;
  }

  public Map<Integer, TelegramPerson> map(final MessageMetadata metadata) {
    final var strings = metadata.command()
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
    final var mentions = metadata.entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.MENTION))
      .filter(entity -> !entity.text().contains(botName))
      .map(entity -> personMapper.mapMessageToTelegram(entity, chatId))
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
      .filter(person -> person.getNickname().equals(nickname))
      .findFirst()
      .orElseThrow(() -> new FinalPlaceContainsNicknameOfNonExistingPlayerException(nickname));
  }

}
