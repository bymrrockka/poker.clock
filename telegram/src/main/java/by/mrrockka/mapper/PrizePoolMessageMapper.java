package by.mrrockka.mapper;

import by.mrrockka.domain.prize.PositionAndPercentage;
import by.mrrockka.domain.prize.PrizePool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PrizePoolMessageMapper {

  public PrizePool map(final String command) {
    final var strings = command.toLowerCase().strip().replaceFirst("/prizepool", "").split("[\n,;%]");
    final var prizePattern = Pattern.compile("^(\\d)([. :\\-=]{1,3})([\\d]{1,3})$");
    return new PrizePool(
      Arrays.stream(strings)
        .map(String::strip)
        .map(prizePattern::matcher)
        .filter(Matcher::matches)
        .map(matcher -> PositionAndPercentage.builder()
          .position(Integer.parseInt(matcher.group(1)))
          .percentage(new BigDecimal(matcher.group(3)))
          .build())
        .toList()
    );
  }
}
