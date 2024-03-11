package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.summary.player.PlayerSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract sealed class AbstractCalculationStrategy<T extends PlayerSummary, G extends Game>
  implements CalculationStrategy
  permits CashCalculationStrategy, TournamentCalculationStrategy {
  @Override
  public List<Payout> calculate(final Game game) {
    final var playersSummaries = buildPlayerSummary(game);


    return playersSummaries.stream()
      .filter(ps -> !ps.getTransferType().equals(TransferType.DEBIT))
      .map(creditorSummary -> {
        final var debtorSummaries = playersSummaries.stream()
          .filter(ps -> ps.getTransferType().equals(TransferType.DEBIT))
          .sorted()
          .toList();

        return calculatePayout(creditorSummary, debtorSummaries);
      }).toList();
  }

  protected G castToType(final Game game) {
    return (G) game;
  }

  protected abstract List<T> buildPlayerSummary(final Game game);

  protected abstract Payout buildPayoutBase(final T creditorSummary);

  protected abstract Debt buildDebtBase(final T debtorSummary);

  private Payout calculatePayout(final T creditorSummary,
                                 final List<T> debtorSummaries) {

    final var debts = new ArrayList<Debt>();
    var leftToPay = creditorSummary.getTransferAmount();
    final var payoutbuilder = buildPayoutBase(creditorSummary).toBuilder();

    if (creditorSummary.getTransferType().equals(TransferType.EQUAL)) {
      return payoutbuilder.debts(Collections.emptyList()).build();
    }

    for (final var debtorSummary : debtorSummaries) {
      final var debtAmount = debtorSummary.getTransferAmount();
      final var debtBuilder = buildDebtBase(debtorSummary).toBuilder();
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
