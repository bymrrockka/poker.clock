package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.payout.TransferType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@EqualsAndHashCode
public abstract sealed class PlayerSummary implements Comparable<PlayerSummary>
  permits TournamentPlayerSummary, CashPlayerSummary, BountyPlayerSummary {

  @NonNull
  private final PersonEntries personEntries;
  @NonNull
  private BigDecimal transferAmount;
  @NonNull
  private final TransferType transferType;

  protected PlayerSummary(@NonNull final PersonEntries personEntries, final BigDecimal transferAmount,
                          @NonNull final TransferType transferType) {
    this.personEntries = personEntries;
    this.transferAmount = Optional.ofNullable(transferAmount).orElse(BigDecimal.ZERO);
    this.transferType = transferType;
  }

  public void subtractCalculated(final BigDecimal calculated) {
    this.transferAmount = this.transferAmount.subtract(calculated);
  }

  @Override
  public int compareTo(final PlayerSummary ps) {
    return ps.getTransferAmount().compareTo(this.getTransferAmount());
  }

  public Person getPerson() {
    return getPersonEntries().person();
  }

}
