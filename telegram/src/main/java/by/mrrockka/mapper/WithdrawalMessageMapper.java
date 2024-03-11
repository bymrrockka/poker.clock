package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
public class WithdrawalMessageMapper {

  private static final String WITHDRAWAL_REGEX = "^/withdrawal([ ]+)%s([ ]+)([\\d]+)$".formatted(TELEGRAM_NAME_REGEX);

  public Pair<String, BigDecimal> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(WITHDRAWAL_REGEX).matcher(str);
    if (matcher.matches()) {
      return Pair.of(matcher.group(2), new BigDecimal(matcher.group(4)));
    }

    throw new InvalidMessageFormatException(WITHDRAWAL_REGEX);
  }
}
