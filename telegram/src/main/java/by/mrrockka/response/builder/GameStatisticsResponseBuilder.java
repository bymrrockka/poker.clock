package by.mrrockka.response.builder;

import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class GameStatisticsResponseBuilder {

  public String response(final Game game) {
    final var strBuilder = new StringBuilder("Game statistics:");

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("players entered")
      .append(POINTER)
      .append(game.getEntries().size());

    final var entries = game.getEntries().stream()
      .flatMap(entry -> entry.entries().stream())
      .toList();

    final var entriesTotal = entries.stream()
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (game instanceof TournamentGame) {
      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("number of entries")
        .append(POINTER)
        .append(entries.size());
    }

    if (game.isType(CashGame.class)) {
      addTotalBuyInAmount(entriesTotal, strBuilder);
      final var withdrawalsTotal = game.asType(CashGame.class).getWithdrawals().stream()
        .flatMap(entry -> entry.withdrawals().stream())
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("total withdrawal amount")
        .append(POINTER)
        .append(withdrawalsTotal);
    }

    if (game.isType(TournamentGame.class)) {
      addTotalBuyInAmount(entriesTotal, strBuilder);
    }

    if (game.isType(BountyGame.class)) {
      final var bountyGame = game.asType(BountyGame.class);
      final var entriesWithBountiesAmount = entriesTotal.add(
        bountyGame.getBountyAmount().multiply(BigDecimal.valueOf(entries.size())));
      addTotalBuyInAmount(entriesWithBountiesAmount, strBuilder);

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("bounties out of game")
        .append(POINTER)
        .append(bountyGame.getBountyList().size());
    }

    return strBuilder.append(NL).toString();
  }

  private void addTotalBuyInAmount(final BigDecimal amount, final StringBuilder strBuilder) {
    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total buy-in amount")
      .append(POINTER)
      .append(amount);
  }


}
