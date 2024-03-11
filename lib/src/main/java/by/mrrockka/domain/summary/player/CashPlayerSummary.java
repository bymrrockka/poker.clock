package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.payout.TransferType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class CashPlayerSummary extends PlayerSummary {

  @NonNull
  private final Withdrawals withdrawals;

  @Builder(builderMethodName = "cashBuilder", access = AccessLevel.PRIVATE)
  private CashPlayerSummary(@NonNull final Entries entries, final BigDecimal transferAmount,
                            @NonNull final TransferType transferType, @NonNull final Withdrawals withdrawals) {
    super(entries, transferAmount, transferType);
    this.withdrawals = withdrawals;
  }

  public static CashPlayerSummary of(@NonNull final Entries entries, @NonNull final Withdrawals withdrawals) {
    if (!entries.person().equals(withdrawals.person())) {
      throw new PersonsNotMatchingException(entries.person().getNickname(), withdrawals.person().getNickname());
    }

    final var totalEntries = entries.total();
    final var totalWithdrawals = withdrawals.total();
    final var summaryBuilder = cashBuilder()
      .entries(entries)
      .withdrawals(withdrawals);

    if (totalEntries.compareTo(totalWithdrawals) < 0) {
      return summaryBuilder
        .transferType(TransferType.CREDIT)
        .transferAmount(totalWithdrawals.subtract(totalEntries))
        .build();
    }

    if (totalEntries.compareTo(totalWithdrawals) > 0) {
      return summaryBuilder
        .transferType(TransferType.DEBIT)
        .transferAmount(totalEntries.subtract(totalWithdrawals))
        .build();
    }

    return summaryBuilder
      .transferType(TransferType.EQUAL)
      .build();
  }

}
