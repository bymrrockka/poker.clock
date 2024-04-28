package by.mrrockka.response.builder;

import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payout;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class CalculationResponseBuilder {
  private static final String PLUS = " + ";
  private static final String LEFT_PARENTHESIS = " (";
  private static final String RIGHT_PARENTHESIS = ")";
  private static final String DELIMITER = "-----------------------------" + NL;
  private static final String TOTAL = "Total: ";
  private static final String WON_WITH_AMOUNT = "won %s";
  private static final String WITHDRAWAL_WITH_AMOUNT = "withdrawal %s";
  private static final String ENTRIES_WITH_AMOUNT = "entries %s";
  private static final String BOUNTIES_WITH_AMOUNT = "bounties %s";

  public String response(final List<Payout> payouts, final Game game) {
    final var strBuilder = new StringBuilder();
    if (game instanceof CashGame) {
      buildForCash(strBuilder, payouts);
    } else if (game instanceof BountyGame) {
      buildForBountyTournament(strBuilder, payouts, (BountyGame) game);
    } else if (game instanceof TournamentGame) {
      buildForTournament(strBuilder, payouts, (TournamentGame) game);
    }
    return strBuilder.toString();
  }

  private void buildForTournament(final StringBuilder strBuilder, final List<Payout> payouts,
                                  final TournamentGame game) {
    addFinaleSummary(strBuilder, game);
    payouts.forEach(payout -> {
      addHeader(strBuilder, payout);
      addEntry(strBuilder, payout.personEntries().entries().size());
      addTotalForTournament(strBuilder, payout, game);
      addPayers(strBuilder, payout);
    });
  }

  private void buildForCash(final StringBuilder strBuilder, final List<Payout> payouts) {
    payouts.forEach(payout -> {
      addHeader(strBuilder, payout);
      addEntry(strBuilder, payout.personEntries().total());
      addWithdrawal(strBuilder, payout);
      addTotalForCash(strBuilder, payout);
      addPayers(strBuilder, payout);
    });
  }

  private void buildForBountyTournament(final StringBuilder strBuilder, final List<Payout> payouts,
                                        final BountyGame game) {
    addFinaleSummary(strBuilder, game);
    payouts.forEach(payout -> {
      addHeader(strBuilder, payout);
      addEntry(strBuilder, payout.personEntries().entries().size());
      addBounties(strBuilder, payout);
      addTotalForBountyTournament(strBuilder, payout, game);
      addPayers(strBuilder, payout);
    });
  }

  private void addHeader(final StringBuilder strBuilder, final Payout payout) {
    strBuilder.append(DELIMITER);
    strBuilder.append("Payout to: @");
    strBuilder.append(payout.person().getNickname());
    strBuilder.append(NL);
  }

  private void addTotalForTournament(final StringBuilder strBuilder, final Payout payout, final TournamentGame game) {
    strBuilder.append(TAB);
    strBuilder.append(TOTAL);
    strBuilder.append(payout.total());
    strBuilder.append(LEFT_PARENTHESIS);
    strBuilder.append(WON_WITH_AMOUNT.formatted(game.getFinaleSummary().getPrizeFor(payout.person())));
    strBuilder.append(MINUS);
    strBuilder.append(ENTRIES_WITH_AMOUNT.formatted(payout.personEntries().total()));
    strBuilder.append(RIGHT_PARENTHESIS);
    strBuilder.append(NL);
  }

  private void addTotalForBountyTournament(final StringBuilder strBuilder, final Payout payout, final BountyGame game) {
    final var isNegative = payout.personBounties().total().compareTo(BigDecimal.ZERO) < 0;
    strBuilder.append(TAB);
    strBuilder.append(TOTAL);
    strBuilder.append(payout.total());
    strBuilder.append(LEFT_PARENTHESIS);
    strBuilder.append(WON_WITH_AMOUNT.formatted(game.getFinaleSummary().getPrizeFor(payout.person())));
    strBuilder.append(MINUS);
    strBuilder.append(ENTRIES_WITH_AMOUNT.formatted(payout.personEntries().total()));
    strBuilder.append(isNegative ? MINUS : PLUS);
    strBuilder.append(BOUNTIES_WITH_AMOUNT.formatted(payout.personBounties().total()));
    strBuilder.append(RIGHT_PARENTHESIS);
    strBuilder.append(NL);
  }

  private void addTotalForCash(final StringBuilder strBuilder, final Payout payout) {
    strBuilder.append(TAB);
    strBuilder.append(TOTAL);
    strBuilder.append(payout.total());
    strBuilder.append(LEFT_PARENTHESIS);
    strBuilder.append(WITHDRAWAL_WITH_AMOUNT.formatted(payout.personWithdrawals().total()));
    strBuilder.append(MINUS);
    strBuilder.append(ENTRIES_WITH_AMOUNT.formatted(payout.personEntries().total()));
    strBuilder.append(RIGHT_PARENTHESIS);
    strBuilder.append(NL);
  }

  private void addPayers(final StringBuilder strBuilder, final Payout payout) {
    final var strDebtsOpt = payout.payers().stream()
      .map(debt -> "\t@%s -> %s".formatted(debt.person().getNickname(), debt.amount()))
      .reduce("%s\n%s"::formatted);

    if (strDebtsOpt.isPresent()) {
      strBuilder.append("From");
      strBuilder.append(NL);
      strBuilder.append(strDebtsOpt.get());
      strBuilder.append(NL);
    }
  }

  private void addEntry(final StringBuilder strBuilder, final int value) {
    addEntry(strBuilder, BigDecimal.valueOf(value));
  }

  private void addEntry(final StringBuilder strBuilder, final BigDecimal value) {
    strBuilder.append(TAB);
    strBuilder.append("Entries: ");
    strBuilder.append(value);
    strBuilder.append(NL);
  }

  private void addWithdrawal(final StringBuilder strBuilder, final Payout payout) {
    strBuilder.append(TAB);
    strBuilder.append("Withdrawals: ");
    strBuilder.append(payout.personWithdrawals().total());
    strBuilder.append(NL);
  }

  private void addBounties(final StringBuilder strBuilder, final Payout payout) {
    final var totalBounties = payout.personBounties().totalTaken().subtract(payout.personBounties().totalGiven());
    strBuilder.append(TAB);
    strBuilder.append("Bounties: ");
    strBuilder.append(totalBounties);
    strBuilder.append(LEFT_PARENTHESIS);

    if (!payout.personBounties().taken().isEmpty()) {
      strBuilder.append("taken ");
      strBuilder.append(payout.personBounties().taken().size());
    }

    if (!payout.personBounties().taken().isEmpty() && !payout.personBounties().given().isEmpty()) {
      strBuilder.append(" - ");
    }

    if (!payout.personBounties().given().isEmpty()) {
      strBuilder.append("given ");
      strBuilder.append(payout.personBounties().given().size());
    }
    strBuilder.append(RIGHT_PARENTHESIS);
    strBuilder.append(NL);
  }

  private void addFinaleSummary(final StringBuilder strBuilder, final TournamentGame game) {
    final var placesStr = game.asType(TournamentGame.class).getFinaleSummary().finaleSummaries().stream()
      .map(place -> "%s. @%s won %s".formatted(place.position(), place.person().getNickname(), place.amount()))
      .reduce("%s\n%s"::formatted);

    if (placesStr.isPresent()) {
      strBuilder.append(DELIMITER);

      strBuilder.append("Finale places:");
      strBuilder.append(NL);
      strBuilder.append(placesStr.get());
      strBuilder.append(NL);

      strBuilder.append(TOTAL);
      strBuilder.append(game.getFinaleSummary().total());
      strBuilder.append(LEFT_PARENTHESIS);
      strBuilder.append(game.getEntries().size());
      strBuilder.append(" entries * ");
      strBuilder.append(game.getBuyIn());
      strBuilder.append(" buy in");
      strBuilder.append(RIGHT_PARENTHESIS);
      strBuilder.append(NL);
    }
  }
}
