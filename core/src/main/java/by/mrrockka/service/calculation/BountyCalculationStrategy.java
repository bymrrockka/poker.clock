package by.mrrockka.service.calculation;

import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.player.BountyPlayerSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class BountyCalculationStrategy extends AbstractCalculationStrategyTemplateMethod<BountyPlayerSummary, BountyGame> {
  @Override
  protected List<BountyPlayerSummary> buildPlayerSummary(final Game game) {
    final var bountyGame = game.asType(BountyGame.class);
    return bountyGame.getEntries().stream()
      .map(entry -> BountyPlayerSummary.of(entry, bountyGame.getBountyList(), bountyGame.getFinaleSummary()))
      .sorted()
      .toList();
  }

  @Override
  protected Payout buildPayoutBase(final BountyPlayerSummary creditorSummary) {
    return Payout.builder()
      .personEntries(creditorSummary.getPersonEntries())
      .personBounties(creditorSummary.getPersonBounties())
      .build();
  }

  @Override
  protected Payer buildPayerBase(final BountyPlayerSummary debtorSummary) {
    return Payer.builder()
      .personEntries(debtorSummary.getPersonEntries())
      .personBounties(debtorSummary.getPersonBounties())
      .build();
  }
}