package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.summary.TournamentSummary;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class TournamentPlayerSummary extends PlayerSummary {

  @Builder(builderMethodName = "tournamentBuilder")
  private TournamentPlayerSummary(@NonNull final Entries entries, final BigDecimal transferAmount,
                                  @NonNull final TransferType transferType) {
    super(entries, transferAmount, transferType);
  }

  public static TournamentPlayerSummary of(@NonNull final Entries entries,
                                           @NonNull final TournamentSummary tournamentSummary) {

    final var playerTotal = entries.total();
    final var person = entries.person();
    final var summaryBuilder = tournamentBuilder().entries(entries);

    if (tournamentSummary.isInPrizes(person)) {
      final var prizeAmount = tournamentSummary.getPrizeFor(person);
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

}
