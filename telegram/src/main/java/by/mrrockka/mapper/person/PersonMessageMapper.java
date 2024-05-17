package by.mrrockka.mapper.person;

import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.validation.mentions.NoPlayersException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class PersonMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.nickname}")
  @Setter
  private String botName;

  @Deprecated(since = "1.1.0", forRemoval = true)
  public List<TelegramPerson> map(final String command, final Long chatId) {
    final var strings = command.toLowerCase()
      .stripTrailing()
      .split("(, |[\n ])");
    final var telegramPattern = Pattern.compile(TELEGRAM_NAME_REGEX);

    final var persons = Arrays.stream(strings)
      .distinct()
      .filter(StringUtils::isNotBlank)
      .filter(str -> !str.contains(botName))
      .map(telegramPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> TelegramPerson.telegramPersonBuilder()
        .id(UUID.randomUUID())
        .nickname(matcher.group(1))
        .chatId(chatId)
        .build())
      .toList();

    if (persons.isEmpty() || persons.size() == 1) {
      throw new NoPlayersException();
    }

    return persons;
  }

  public List<TelegramPerson> map(final MessageMetadata messageMetadata) {
//    todo: verify if there is a chance to add filter to message metadata (at least partially)
    final var playersMentions = messageMetadata.entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.MENTION))
      .filter(entity -> !entity.text().contains(botName))
      .toList();

    return personMapper.mapMessageToTelegramPersons(playersMentions, messageMetadata.chatId());
  }

}
