package by.mrrockka.validation.calculation;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Component
public class CalculationValidator {

  public void validateGame(final Game game) {
    if (game.isType(CashGame.class)) {
      validateCash(game.asType(CashGame.class));
    }

    if (game instanceof TournamentGame) {
      validateTournament(game.asType(TournamentGame.class));
    }

    if (game.isType(BountyGame.class)) {
      validateBounty(game.asType(BountyGame.class));
    }
  }

  private void validateTournament(final TournamentGame game) {
    if (isNull(game.getFinaleSummary())) {
      throw new FinaleSummaryNotFoundException();
    }
  }

  private void validateBounty(final BountyGame game) {
    final var bountiesCount = game.getBountyList().size() + 1;
    final var entriesCount = game.getEntries().stream()
      .mapToInt(entry -> entry.entries().size())
      .sum();

    if (entriesCount != bountiesCount) {
      throw new BountiesAndEntriesSizeAreNotEqualException(entriesCount - bountiesCount);
    }
  }

  private void validateCash(final CashGame game) {
    final var totalEntries = game.getEntries().stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var totalWithdrawals = game.getWithdrawals().stream()
      .map(PersonWithdrawals::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (totalEntries.compareTo(totalWithdrawals) != 0) {
      throw new EntriesAndWithdrawalAmountsAreNotEqualException(totalEntries.subtract(totalWithdrawals));
    }
  }

}
