package by.mrrockka.mapper;

import by.mrrockka.domain.prize.PositionAndPercentage;
import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PrizePoolMessageMapper {

  private static final String PRIZE_POOL_REGEX = "^(\\d)([. :\\-=]{1,3})([\\d]{1,3})$";
  private static final int POSITION_GROUP = 1;
  private static final int AMOUNT_GROUP = 3;
  private static final String ERROR_MESSAGE = "/prizepool 1. 100 (, #places #amount)";

  public PrizePool map(final String command) {
    final var strings = command.toLowerCase().strip().replaceFirst("/prizepool([ \n]*)", "").split("[\n,;%]");
    final var prizePattern = Pattern.compile(PRIZE_POOL_REGEX);
    final var result = new PrizePool(
      Arrays.stream(strings)
        .map(String::strip)
        .map(prizePattern::matcher)
        .filter(Matcher::matches)
        .map(matcher -> PositionAndPercentage.builder()
          .position(Integer.parseInt(matcher.group(POSITION_GROUP)))
          .percentage(new BigDecimal(matcher.group(AMOUNT_GROUP)))
          .build())
        .toList()
    );

    if (result.positionAndPercentages().isEmpty()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    return result;
  }
}
