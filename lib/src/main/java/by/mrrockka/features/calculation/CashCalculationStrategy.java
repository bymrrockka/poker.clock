package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.player.CashPlayerSummary;

import java.util.List;

public final class CashCalculationStrategy extends AbstractCalculationStrategy<CashPlayerSummary, CashGame> {
  @Override
  public boolean isApplicable(final Game game) {
    return game instanceof CashGame;
  }

  @Override
  protected List<CashPlayerSummary> buildPlayerSummary(final Game game) {
    final var cash = castToType(game);
    return cash.getEntries().stream()
      .map(entry -> CashPlayerSummary.of(
        entry,
        cash.getWithdrawals().stream()
          .filter(withdrawal -> withdrawal.person().equals(entry.person()))
          .findFirst()
          .orElse(null)))
      .sorted()
      .toList();
  }

  @Override
  protected Payout buildPayoutBase(final CashPlayerSummary creditorSummary) {
    return Payout.builder()
      .entries(creditorSummary.getEntries())
      .withdrawals(creditorSummary.getWithdrawals())
      .build();
  }

  @Override
  protected Debt buildDebtBase(final CashPlayerSummary debtorSummary) {
    return Debt.builder()
      .entries(debtorSummary.getEntries())
      .withdrawals(debtorSummary.getWithdrawals())
      .build();
  }
}