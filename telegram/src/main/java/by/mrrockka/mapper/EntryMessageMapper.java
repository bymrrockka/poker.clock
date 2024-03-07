package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class EntryMessageMapper {

  private static final String ENTRY_REGEX = "^/(entry|reentry) @([A-z]+)(([ :\\-=]{1,3})([\\d]+)|)$";

  public Pair<String, Optional<BigDecimal>> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(ENTRY_REGEX).matcher(str);
    if (matcher.matches()) {
      final var amount = matcher.groupCount() > 4 ? matcher.group(5) : null;
      return Pair.of(matcher.group(2), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new InvalidMessageFormatException(ENTRY_REGEX);
  }
}
