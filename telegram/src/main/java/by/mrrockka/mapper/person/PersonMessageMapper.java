package by.mrrockka.mapper.person;

import by.mrrockka.domain.TelegramPerson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PersonMessageMapper {

  public List<TelegramPerson> map(String command, Long chatId) {
    final var strings = command.toLowerCase()
      .stripTrailing()
      .split("(, |[\n ])");
    final var telegramPattern = Pattern.compile("^@([\\w.]+)");

    final var persons = Arrays.stream(strings)
      .distinct()
      .filter(StringUtils::isNotBlank)
      .map(telegramPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> TelegramPerson.telegramPersonBuilder()
        .id(UUID.randomUUID())
        .telegram(matcher.group(1))
        .chatId(chatId)
        .build())
      .toList();

    if (persons.isEmpty() || persons.size() == 1) {
      throw new NoPlayersException();
    }

    return persons;
  }

}
