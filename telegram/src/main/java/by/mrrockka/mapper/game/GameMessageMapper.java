package by.mrrockka.mapper.game;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.GameType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameMessageMapper {

  public static final String BUY_IN_REGEX = "^buy([-_ ]*)in:([\\d]+)([A-z]*)$";
  public static final String STACK_REGEX = "^stack:([.\\d]+)([A-z]|)";

  public Game map(final String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return Game.gameBuilder()
      .id(UUID.randomUUID())
      .gameType(GameType.TOURNAMENT)
      .buyIn(mapBuyIn(strings))
      .stack(mapStack(strings))
      .build();
  }

  private BigDecimal mapBuyIn(final String[] strings) {
    final var buyInPattern = Pattern.compile(BUY_IN_REGEX);
    return Arrays.stream(strings)
      .map(buyInPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> matcher.group(2))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(() -> new NoBuyInException(BUY_IN_REGEX));
  }

  private BigDecimal mapStack(final String[] strings) {
    final var stackPattern = Pattern.compile(STACK_REGEX);
    return Arrays.stream(strings)
      .map(stackPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> extractStack(matcher.group(1), StringUtils.isNotBlank(matcher.group(2))))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(() -> new NoStackException(STACK_REGEX));
  }

  private Double extractStack(final String value, final boolean shouldMultiply) {
    final var val = Double.parseDouble(value);
    return shouldMultiply ? val * 1000 : val;
  }

}
