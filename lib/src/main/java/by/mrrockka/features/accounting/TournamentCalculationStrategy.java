package by.mrrockka.features.accounting;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.summary.EntriesSummary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TournamentCalculationStrategy implements CalculationStrategy {
  @Override
  public List<Payout> calculate(final Game game) {
    final var tournament = this.<TournamentGame>castToType(game);

    final var playerSummaries = tournament.getEntries()
      .stream()
      .distinct()
      .map(entries -> EntriesSummary.of(entries, tournament.getTournamentGameSummary()))
      .sorted()
      .toList();

    return playerSummaries.stream()
      .filter(ps -> !ps.getTransferType().equals(TransferType.DEBIT))
      .map(creditorSummary -> {
        final var debtorSummaries = playerSummaries.stream()
          .filter(ps -> ps.getTransferType().equals(TransferType.DEBIT))
          .filter(ps -> (ps.getTransferAmount().compareTo(BigDecimal.ZERO) != 0))
          .sorted()
          .toList();

        return calculatePayouts(creditorSummary, debtorSummaries);
      }).toList();
  }

  @Override
  public boolean isApplicable(final Game game) {
    return game instanceof TournamentGame;
  }

  private Payout calculatePayouts(final EntriesSummary creditorSummary, final List<EntriesSummary> debtorSummaries) {
    final var debts = new ArrayList<Debt>();
    var leftToPay = creditorSummary.getTransferAmount();
    final var payoutbuilder = Payout.builder()
      .creditorEntries(creditorSummary.getEntries());

    if (creditorSummary.getTransferType().equals(TransferType.EQUAL)) {
      return payoutbuilder.debts(Collections.emptyList()).build();
    }

    for (final var debtorSummary : debtorSummaries) {
      final var debtAmount = debtorSummary.getTransferAmount();
      final var debtBuilder = Debt.builder()
        .debtorEntries(debtorSummary.getEntries());

      final var debtComparison = debtAmount.compareTo(leftToPay);

      if (debtComparison == 0) {
        debtorSummary.subtractCalculated(debtAmount);
        debts.add(debtBuilder
                    .amount(debtAmount)
                    .build());
        break;
      }

      if (debtComparison < 0) {
        debtorSummary.subtractCalculated(debtAmount);
        debts.add(debtBuilder
                    .amount(debtAmount)
                    .build());
        leftToPay = leftToPay.subtract(debtAmount);
      }

      if (debtComparison > 0) {
        debtorSummary.subtractCalculated(leftToPay);
        debts.add(debtBuilder
                    .amount(leftToPay)
                    .build());
        break;
      }
    }

    return payoutbuilder.debts(debts).build();
  }

}
