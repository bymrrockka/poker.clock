package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@Deprecated(forRemoval = true)
public final class BountyPlayerSummary extends PlayerSummary {

  @NonNull
  private final PersonBounties personBounties;

  @Builder(builderMethodName = "bountySummaryBuilder", access = AccessLevel.PRIVATE)
  private BountyPlayerSummary(@NonNull final PersonEntries personEntries, final BigDecimal transferAmount,
                              @NonNull final TransferType transferType, @NonNull final PersonBounties personBounties) {
    super(personEntries, transferAmount, transferType);
    this.personBounties = personBounties;
  }

  public static BountyPlayerSummary of(@NonNull final PersonEntries personEntries,
                                       @NonNull final List<Bounty> bountyList,
                                       @NonNull final FinaleSummary finaleSummary) {
    final var personBounties = PersonBounties.builder()
      .person(personEntries.person())
      .bounties(bountyList.stream()
                  .filter(bounty -> bounty.from().equals(personEntries.person())
                    || bounty.to().equals(personEntries.person()))
                  .toList())
      .build();

    final var bountiesTotal = personBounties.totalTaken().subtract(personBounties.totalGiven());
    final var playerTotal = finaleSummary.calculateSummaryAmount(personEntries.person(), personEntries.total());
    final var summaryTotal = playerTotal.add(bountiesTotal);

    final var summaryBuilder = bountySummaryBuilder()
      .personEntries(personEntries)
      .personBounties(personBounties);

    if (summaryTotal.compareTo(BigDecimal.ZERO) > 0) {
      return summaryBuilder
        .transferType(TransferType.CREDIT)
        .transferAmount(summaryTotal)
        .build();
    }
    if (summaryTotal.compareTo(BigDecimal.ZERO) < 0) {
      return summaryBuilder
        .transferType(TransferType.DEBIT)
        .transferAmount(summaryTotal.negate())
        .build();
    }

    return summaryBuilder
      .transferType(TransferType.EQUAL)
      .build();
  }

}
