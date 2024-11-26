package by.mrrockka.parser.game;

import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.TournamentGame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameMessageParser {

  private static final String AMOUNT_REGEX = "([.\\d]+)([A-z]|)";
  private static final String BUY_IN_REGEX = "^buyin:%s$".formatted(AMOUNT_REGEX);
  private static final String STACK_REGEX = "^stack:%s$".formatted(AMOUNT_REGEX);
  private static final String BOUNTY_REGEX = "^bounty:%s$".formatted(AMOUNT_REGEX);

  public TournamentGame parseTournamentGame(final String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return TournamentGame.tournamentBuilder()
      .id(UUID.randomUUID())
      .buyIn(parseBuyIn(strings))
      .stack(parseStack(strings))
      .build();
  }

  public CashGame parseCashGame(final String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return CashGame.cashBuilder()
      .id(UUID.randomUUID())
      .buyIn(parseBuyIn(strings))
      .stack(parseStack(strings))
      .build();
  }

  public BountyGame parseBountyGame(final String command) {
    final var strings = command.toLowerCase().replaceAll(" ", "").split("\n");

    return BountyGame.bountyBuilder()
      .id(UUID.randomUUID())
      .buyIn(parseBuyIn(strings))
      .stack(parseStack(strings))
      .bountyAmount(parseBounty(strings))
      .build();
  }

  private BigDecimal parseBuyIn(final String[] strings) {
    final var buyInPattern = Pattern.compile(BUY_IN_REGEX);
    return Arrays.stream(strings)
      .map(buyInPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> matcher.group(1))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(GameFieldIsNotSpecifiedException::buyin);
  }

  private BigDecimal parseStack(final String[] strings) {
    final var stackPattern = Pattern.compile(STACK_REGEX);
    return Arrays.stream(strings)
      .map(stackPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> parseStack(matcher.group(1), StringUtils.isNotBlank(matcher.group(2))))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(GameFieldIsNotSpecifiedException::stack);
  }

  private BigDecimal parseBounty(final String[] strings) {
    final var stackPattern = Pattern.compile(BOUNTY_REGEX);
    return Arrays.stream(strings)
      .map(stackPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> parseStack(matcher.group(1), StringUtils.isNotBlank(matcher.group(2))))
      .map(BigDecimal::new)
      .findFirst()
      .orElseThrow(GameFieldIsNotSpecifiedException::bounty);
  }

  private Double parseStack(final String value, final boolean shouldMultiply) {
    final var val = Double.parseDouble(value);
    return shouldMultiply ? val * 1000 : val;
  }

}
