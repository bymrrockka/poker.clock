package by.mrrockka.mapper;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class EntryMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.name}")
  @Setter
  private String botName;

  private static final String ENTRY_REGEX = "^/entry %s([@A-z0-9]*)([ ]*)([\\d]*)$".formatted(TELEGRAM_NAME_REGEX);
  private static final int MENTION_GROUP = 2;
  private static final int AMOUNT_GROUP = 4;

  private static final String ERROR_MESSAGE = "/entry @nickname (amount)";

  public Pair<String, Optional<BigDecimal>> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(str);
    if (matcher.matches()) {
      final var amount = StringUtils.isNotBlank(matcher.group(AMOUNT_GROUP)) ? matcher.group(AMOUNT_GROUP) : null;
      return Pair.of(matcher.group(MENTION_GROUP), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

  public Pair<String, Optional<BigDecimal>> map(final MessageMetadata metadata) {
    final var command = metadata.command().toLowerCase().strip();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(command);
    if (matcher.matches()) {
      final var amount = StringUtils.isNotBlank(matcher.group(AMOUNT_GROUP)) ? matcher.group(AMOUNT_GROUP) : null;
      return Pair.of(matcher.group(MENTION_GROUP), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

//  todo: use entities
}
