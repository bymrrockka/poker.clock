package by.mrrockka.parser;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.parser.CommandRegexConstants.TELEGRAM_NAME_REGEX;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Component
@RequiredArgsConstructor
public class EntryMessageParser {

  private final TelegramPersonMapper personMapper;

  private static final String ENTRY_REGEX = "^/entry(( %s)+)([ ]*)([\\d]*)$".formatted(TELEGRAM_NAME_REGEX);
  private static final int AMOUNT_GROUP = 5;
  private static final String ERROR_MESSAGE = "/entry @nickname (amount)";

  public Map<TelegramPerson, Optional<BigDecimal>> parse(final MessageMetadata metadata) {
    final var command = metadata.getText().toLowerCase().strip();
    final var chatId = metadata.getChatId();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(command);
    if (matcher.matches()) {
      final var optAmount = Optional.ofNullable(defaultIfBlank(matcher.group(AMOUNT_GROUP), null))
        .map(BigDecimal::new);

      return metadata.mentions()
        .map(entity -> personMapper.mapMessageToTelegramPerson(entity, chatId))
        .collect(Collectors.toMap(Function.identity(), p -> optAmount));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

}
