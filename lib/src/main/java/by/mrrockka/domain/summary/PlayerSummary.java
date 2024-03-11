package by.mrrockka.domain.summary;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.payout.TransferType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
public class PlayerSummary implements Comparable<PlayerSummary> {

  @NonNull
  private Player player;
  @NonNull
  @Builder.Default
  private BigDecimal transferAmount = BigDecimal.ZERO;
  @NonNull
  private TransferType transferType;

  public void subtractCalculated(final BigDecimal calculated) {
    this.transferAmount = this.transferAmount.subtract(calculated);
  }

  @Override
  public int compareTo(final PlayerSummary ps) {
    return ps.getTransferAmount().compareTo(this.getTransferAmount());
  }

  //todo: consider refactoring to separate service in case there will be additional implementations
  public static PlayerSummary of(@NonNull final Player player,
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

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof final PlayerSummary that))
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
