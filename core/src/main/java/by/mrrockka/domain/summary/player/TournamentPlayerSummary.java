package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = true)
@Deprecated(forRemoval = true)
public final class TournamentPlayerSummary extends PlayerSummary {

  @Builder(builderMethodName = "tournamentSummaryBuilder", access = AccessLevel.PRIVATE)
  private TournamentPlayerSummary(@NonNull final PersonEntries personEntries, final BigDecimal transferAmount,
                                  @NonNull final TransferType transferType) {
    super(personEntries, transferAmount, transferType);
  }

  public static TournamentPlayerSummary of(@NonNull final PersonEntries personEntries,
                                           @NonNull final FinaleSummary finaleSummary) {

    final var summaryBuilder = tournamentSummaryBuilder().personEntries(personEntries);
    final var summaryTotal = finaleSummary.calculateSummaryAmount(personEntries.person(), personEntries.total());

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
