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

  public void subtractCalculated(BigDecimal calculated) {
    this.transferAmount = this.transferAmount.subtract(calculated);
  }

  @Override
  public int compareTo(PlayerSummary ps) {
    return ps.getTransferAmount().compareTo(this.getTransferAmount());
  }

  //todo: consider refactoring to separate service in case there will be additional implementations
  public static PlayerSummary of(@NonNull Player player, @NonNull GameSummary gameSummary) {

    final var playerTotal = player.payments().total();
    final var person = player.person();
    var playerBuilder = PlayerSummary.builder().player(player);

    if (gameSummary.isInPrizes(person)) {
      final var prizeAmount = gameSummary.getPrizeFor(person);
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
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PlayerSummary that))
      return false;
    return Objects.equals(player, that.player)
      && Objects.equals(transferAmount, that.transferAmount)
      && transferType == that.transferType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(player, transferAmount, transferType);
  }
}
