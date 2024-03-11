package by.mrrockka.domain.summary.player;

import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.creator.WithdrawalsCreator;
import by.mrrockka.domain.payout.TransferType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CashPlayerSummaryTest {

  @Test
  void givenEntriesAndWithdrawalsList_whenBuilderCalled_thenSummaryCreated() {
    final var person = PersonCreator.domain();
    final var entries = EntriesCreator.entries(builder -> builder.person(person));
    final var withdrawals = WithdrawalsCreator.withdrawals(builder -> builder.person(person));

    assertThat(CashPlayerSummary.of(entries, withdrawals).getEntries())
      .isEqualTo(entries);

    assertThat(CashPlayerSummary.of(entries, withdrawals).getWithdrawals())
      .isEqualTo(withdrawals);

    assertThat(CashPlayerSummary.of(entries, withdrawals).getTransferType())
      .isEqualTo(TransferType.EQUAL);

    assertThat(CashPlayerSummary.of(entries, withdrawals).getTransferAmount())
      .isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void givenEntriesAndWithdrawalsListWithDifferentPersons_whenBuilderCalled_thenExceptionThrown() {
    final var entries = EntriesCreator.entries(builder -> builder.person(PersonCreator.domainRandom()));
    final var withdrawals = WithdrawalsCreator.withdrawals(builder -> builder.person(PersonCreator.domainRandom()));

    assertThatThrownBy(() -> CashPlayerSummary.of(entries, withdrawals))
      .isInstanceOf(PersonsNotMatchingException.class);
  }
}