package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
public class EntryMessageMapper {

  private static final String ENTRY_REGEX = "^/(entry|reentry)([ ]+)%s([ ]*)([\\d]*)$".formatted(
    TELEGRAM_NAME_REGEX);

  public Pair<String, Optional<BigDecimal>> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(str);
    if (matcher.matches()) {
      final var amount = StringUtils.isNotBlank(matcher.group(5)) ? matcher.group(5) : null;
      return Pair.of(matcher.group(3), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new InvalidMessageFormatException(ENTRY_REGEX);
  }
}
