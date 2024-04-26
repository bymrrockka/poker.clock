package by.mrrockka.domain.summary.player;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.payout.TransferType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class CashPlayerSummary extends PlayerSummary {

  @NonNull
  private final PersonWithdrawals personWithdrawals;

  @Builder(builderMethodName = "cashSummaryBuilder", access = AccessLevel.PRIVATE)
  private CashPlayerSummary(@NonNull final PersonEntries personEntries, final BigDecimal transferAmount,
                            @NonNull final TransferType transferType,
                            @NonNull final PersonWithdrawals personWithdrawals) {
    super(personEntries, transferAmount, transferType);
    this.personWithdrawals = personWithdrawals;
  }

  public static CashPlayerSummary of(@NonNull final PersonEntries personEntries,
                                     @NonNull final List<PersonWithdrawals> personWithdrawalsList) {
    final var personWithdrawals = personWithdrawalsList.stream()
      .filter(withdrawal -> withdrawal.person().equals(personEntries.person()))
      .findFirst()
      .orElse(PersonWithdrawals.builder()
                .person(personEntries.person())
                .withdrawals(Collections.emptyList())
                .build());

    final var totalEntries = personEntries.total();
    final var totalWithdrawals = personWithdrawals.total();
    final var summaryBuilder = cashSummaryBuilder()
      .personEntries(personEntries)
      .personWithdrawals(personWithdrawals);

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
