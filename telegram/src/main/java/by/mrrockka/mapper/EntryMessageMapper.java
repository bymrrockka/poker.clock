package by.mrrockka.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class EntryMessageMapper {

  public Pair<String, Optional<BigDecimal>> map(String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile("^/entry @([A-z]+)(([ :\\-=]{1,3})([\\d]+)|)$").matcher(str);
    if (matcher.matches()) {
      final var amount = matcher.groupCount() > 3 ? matcher.group(4) : null;
      return Pair.of(matcher.group(1), Optional.ofNullable(amount).map(BigDecimal::new));
    }

    throw new RuntimeException(
      "Message is not applicable. Format of the message is ^/entry @([A-z]+)([ :\\-=]{0,3})([\\d]+)$");
  }
}
