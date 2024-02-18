package by.mrrockka.mapper.game;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.GameType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameMessageMapper {

  public Game map(String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return Game.builder()
               .id(UUID.randomUUID())
      .gameType(GameType.TOURNAMENT)
      .buyIn(mapBuyIn(strings))
      .stack(mapStack(strings))
               //todo:
//      .persons(mapPersons(strings))
      .build();
  }

  private List<TelegramPerson> mapPersons(String[] strings) {
    final var telegramPattern = Pattern.compile("^@([\\w]+)");
    final var persons = Arrays.stream(strings)
      .filter(str -> telegramPattern.matcher(str).matches())
      .map(str -> TelegramPerson.builder().telegram(str).build())
      .distinct()
      .toList();
    if (persons.isEmpty() || persons.size() == 1) {
      throw new NoPlayersException();
    }
    return (List<TelegramPerson>) persons;
  }

  private BigDecimal mapBuyIn(String[] strings) {
    final var buyInPattern = Pattern.compile("^(buyin|buy-in):([\\d]+)(([\\w]+)|)$");
    return Arrays.stream(strings)
      .map(buyInPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> matcher.group(2))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(NoBuyInException::new);
  }

  private BigDecimal mapStack(String[] strings) {
    final var stackPattern = Pattern.compile("^stack:([\\.\\d]+)(k|)");
    return Arrays.stream(strings)
      .map(stackPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> extractStack(matcher.group(1), StringUtils.isNotBlank(matcher.group(2))))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(NoStackException::new);
  }

  private Double extractStack(String value, boolean shouldMultiply) {
    final var val = Double.parseDouble(value);
    return shouldMultiply ? val * 1000 : val;
  }

}
