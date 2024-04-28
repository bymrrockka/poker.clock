package by.mrrockka.mapper;

import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Component
@RequiredArgsConstructor
public class EntryMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.nickname}")
  @Setter
  private String botName;

  private static final String ENTRY_REGEX = "^/entry(( %s)+)([ ]*)([\\d]*)$".formatted(TELEGRAM_NAME_REGEX);
  private static final int MENTION_GROUP = 3;
  private static final int AMOUNT_GROUP = 5;
  private static final String ERROR_MESSAGE = "/entry @nickname (amount)";

  @Deprecated(since = "1.1.0", forRemoval = true)
  public Pair<String, Optional<BigDecimal>> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(str);
    if (matcher.matches()) {
      final var amount = StringUtils.isNotBlank(matcher.group(AMOUNT_GROUP)) ? matcher.group(AMOUNT_GROUP) : null;
      return Pair.of(matcher.group(MENTION_GROUP), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

  public Map<TelegramPerson, Optional<BigDecimal>> map(final MessageMetadata metadata) {
    final var command = metadata.command().toLowerCase().strip();
    final var chatId = metadata.chatId();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(command);
    if (matcher.matches()) {
      final var optAmount = Optional.ofNullable(defaultIfBlank(matcher.group(AMOUNT_GROUP), null))
        .map(BigDecimal::new);

      return metadata.entities().stream()
        .filter(entity -> entity.type().equals(MessageEntityType.MENTION))
        .filter(entity -> !entity.text().contains(botName))
        .map(entity -> personMapper.mapMessageToTelegram(entity, chatId))
        .collect(Collectors.toMap(Function.identity(), p -> optAmount));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

}
