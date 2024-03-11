package by.mrrockka.domain.summary;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.payout.TransferType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
public class EntriesSummary implements Comparable<EntriesSummary> {

  private Player player;
  //  todo: make non null
  private Entries entries;
  @NonNull
  @Builder.Default
  private BigDecimal transferAmount = BigDecimal.ZERO;
  @NonNull
  private TransferType transferType;

  public void subtractCalculated(final BigDecimal calculated) {
    this.transferAmount = this.transferAmount.subtract(calculated);
  }

  @Override
  public int compareTo(final EntriesSummary ps) {
    return ps.getTransferAmount().compareTo(this.getTransferAmount());
  }

  //todo: consider refactoring to separate service in case there will be additional implementations
  public static EntriesSummary of(@NonNull final Player player,
                                  @NonNull final TournamentGameSummary tournamentGameSummary) {

    final var playerTotal = player.entries().total();
    final var person = player.person();
    final var playerBuilder = builder().player(player);

    if (tournamentGameSummary.isInPrizes(person)) {
      final var prizeAmount = tournamentGameSummary.getPrizeFor(person);
      if (prizeAmount.compareTo(playerTotal) > 0) {
        return playerBuilder
          .transferType(TransferType.CREDIT)
          .transferAmount(prizeAmount.subtract(playerTotal))
          .build();
      }
      if (prizeAmount.compareTo(playerTotal) < 0) {
        return playerBuilder
          .transferType(TransferType.DEBIT)
          .transferAmount(playerTotal.subtract(prizeAmount))
          .build();
      }
      return playerBuilder
        .transferType(TransferType.EQUAL)
        .build();
    }

    return playerBuilder
      .transferType(TransferType.DEBIT)
      .transferAmount(playerTotal)
      .build();

  }

  public static EntriesSummary of(@NonNull final Entries entries,
                                  @NonNull final TournamentGameSummary tournamentGameSummary) {

    final var playerTotal = entries.total();
    final var person = entries.person();
    final var summaryBuilder = builder().entries(entries);

    if (tournamentGameSummary.isInPrizes(person)) {
      final var prizeAmount = tournamentGameSummary.getPrizeFor(person);
      if (prizeAmount.compareTo(playerTotal) > 0) {
        return summaryBuilder
          .transferType(TransferType.CREDIT)
          .transferAmount(prizeAmount.subtract(playerTotal))
          .build();
      }
      if (prizeAmount.compareTo(playerTotal) < 0) {
        return summaryBuilder
          .transferType(TransferType.DEBIT)
          .transferAmount(playerTotal.subtract(prizeAmount))
          .build();
      }
      return summaryBuilder
        .transferType(TransferType.EQUAL)
        .build();
    }

    return summaryBuilder
      .transferType(TransferType.DEBIT)
      .transferAmount(playerTotal)
      .build();

  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof final EntriesSummary that))
      return false;
    return Objects.equals(player, that.getPlayer())
      && transferAmount.compareTo(that.getTransferAmount()) == 0
      && transferType == that.getTransferType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(player, transferAmount, transferType);
  }
}
