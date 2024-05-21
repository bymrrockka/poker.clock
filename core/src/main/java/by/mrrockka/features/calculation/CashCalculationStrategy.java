package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.player.CashPlayerSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class CashCalculationStrategy extends AbstractCalculationStrategyTemplateMethod<CashPlayerSummary, CashGame> {
  @Override
  public boolean isApplicable(final Game game) {
    return game.isType(CashGame.class);
  }

  @Override
  protected List<CashPlayerSummary> buildPlayerSummary(final Game game) {
    final var cash = game.asType(CashGame.class);
    return cash.getEntries().stream()
      .map(entry -> CashPlayerSummary.of(entry, cash.getWithdrawals()))
      .sorted()
      .toList();
  }

  @Override
  protected Payout buildPayoutBase(final CashPlayerSummary creditorSummary) {
    return Payout.builder()
      .personEntries(creditorSummary.getPersonEntries())
      .personWithdrawals(creditorSummary.getPersonWithdrawals())
      .build();
  }

  @Override
  protected Payer buildPayerBase(final CashPlayerSummary debtorSummary) {
    return Payer.builder()
      .personEntries(debtorSummary.getPersonEntries())
      .personWithdrawals(debtorSummary.getPersonWithdrawals())
      .build();
  }
}