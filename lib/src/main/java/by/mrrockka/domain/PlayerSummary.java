package by.mrrockka.domain;

import by.mrrockka.domain.prize.PrizePool;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
public class PlayerSummary implements Comparable<PlayerSummary> {

  private Player player;
  @Builder.Default
  private BigDecimal transferAmount = BigDecimal.ZERO;
  private TransferType transferType;

  public void subtractCalculated(BigDecimal calculated) {
    this.transferAmount = this.transferAmount.subtract(calculated);
  }

  @Override
  public int compareTo(PlayerSummary ps) {
    return ps.getTransferAmount().compareTo(this.getTransferAmount());
  }

  public static PlayerSummary of(Player player, PrizePool prizePool) {
    Objects.requireNonNull(player, "Player cannot be null");
    Objects.requireNonNull(prizePool, "prizePool cannot be null");

    final var playerTotal = player.payments().total();
    var playerBuilder = PlayerSummary.builder().player(player);

    if (prizePool.isInPrizes(player.position())) {
      final var prizeAmount = prizePool.getPrizeFor(player.position());
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
    if (this == o) return true;
    if (!(o instanceof PlayerSummary that)) return false;
    return Objects.equals(player, that.player)
      && Objects.equals(transferAmount, that.transferAmount)
      && transferType == that.transferType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(player, transferAmount, transferType);
  }
}
