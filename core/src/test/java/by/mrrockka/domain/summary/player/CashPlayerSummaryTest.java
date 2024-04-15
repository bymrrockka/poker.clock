package by.mrrockka.domain.summary.player;

import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.creator.WithdrawalsCreator;
import by.mrrockka.domain.payout.TransferType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CashPlayerSummaryTest {

  @Test
  void givenEntriesAndWithdrawalsList_whenBuilderCalled_thenSummaryCreated() {
    final var person = PersonCreator.domain();
    final var entries = EntriesCreator.entries(builder -> builder.person(person));
    final var withdrawals = WithdrawalsCreator.withdrawals(builder -> builder.person(person));

    assertThat(CashPlayerSummary.of(entries, List.of(withdrawals)).getPersonEntries())
      .isEqualTo(entries);

    assertThat(CashPlayerSummary.of(entries, List.of(withdrawals)).getPersonWithdrawals())
      .isEqualTo(withdrawals);

    assertThat(CashPlayerSummary.of(entries, List.of(withdrawals)).getTransferType())
      .isEqualTo(TransferType.EQUAL);

    assertThat(CashPlayerSummary.of(entries, List.of(withdrawals)).getTransferAmount())
      .isEqualTo(BigDecimal.ZERO);
  }

}