package by.mrrockka.features.calculation;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.player.TournamentPlayerSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class TournamentCalculationStrategy extends AbstractCalculationStrategy<TournamentPlayerSummary, TournamentGame> {

  @Override
  public boolean isApplicable(final Game game) {
    return game instanceof TournamentGame;
  }

  @Override
  protected List<TournamentPlayerSummary> buildPlayerSummary(final Game game) {
    final var tournament = castToType(game);
    return tournament.getEntries()
      .stream()
      .map(entries -> TournamentPlayerSummary.of(entries, tournament.getTournamentSummary()))
      .sorted()
      .toList();
  }

  @Override
  protected Payout buildPayoutBase(final TournamentPlayerSummary creditorSummary) {
    return Payout.builder()
      .entries(creditorSummary.getEntries())
      .build();
  }

  @Override
  protected Debt buildDebtBase(TournamentPlayerSummary debtorSummary) {
    return Debt.builder()
      .entries(debtorSummary.getEntries())
      .build();
  }

}
