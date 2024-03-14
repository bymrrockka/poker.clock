package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.player.TournamentPlayerSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class TournamentCalculationStrategy extends AbstractCalculationStrategy<TournamentPlayerSummary, TournamentGame> {

  @Override
  public boolean isApplicable(final Game game) {
    return game.isTournament();
  }

  @Override
  protected List<TournamentPlayerSummary> buildPlayerSummary(final Game game) {
    final var tournament = game.asTournament();
    return tournament.getEntries()
      .stream()
      .map(entries -> TournamentPlayerSummary.of(entries, tournament.getFinaleSummary()))
      .sorted()
      .toList();
  }

  @Override
  protected Payout buildPayoutBase(final TournamentPlayerSummary creditorSummary) {
    return Payout.builder()
      .personEntries(creditorSummary.getPersonEntries())
      .build();
  }

  @Override
  protected Payer buildDebtBase(TournamentPlayerSummary debtorSummary) {
    return Payer.builder()
      .personEntries(debtorSummary.getPersonEntries())
      .build();
  }

}
