package by.mrrockka.mapper;

import by.mrrockka.model.Game;
import by.mrrockka.model.GameType;
import by.mrrockka.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameMapper {

  public Game map(String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return Game.builder()
      .gameType(GameType.TOURNAMENT)
      .buyIn(mapBuyIn(strings))
      .stack(mapStack(strings))
      .persons(mapPersons(strings))
      .build();
  }

  private List<Person> mapPersons(String[] strings) {
    final var telegramPattern = Pattern.compile("^@([\\w]+)");
    return Arrays.stream(strings)
      .filter(str -> telegramPattern.matcher(str).matches())
      .map(str -> Person.builder().telegram(str).build())
      .toList();
  }

  private BigDecimal mapBuyIn(String[] strings) {
    final var buyInPattern = Pattern.compile("^(buyin|buy-in):([\\d]+)");
    return Arrays.stream(strings)
      .map(buyInPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> matcher.group(2))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("No buy-in specified."));
  }

  private BigDecimal mapStack(String[] strings) {
    final var stackPattern = Pattern.compile("^stack:([\\d]+)(k|)");
    return Arrays.stream(strings)
      .map(stackPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> extractStack(matcher.group(1), StringUtils.isNotBlank(matcher.group(2))))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("No buy-in specified."));
  }

  private Double extractStack(String value, boolean shouldMultiply) {
    final var val = Double.parseDouble(value);
    return shouldMultiply ? val * 1000 : val;
  }

}
