package by.mrrockka.features.calculation;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static by.mrrockka.creator.EntriesCreator.entries;
import static by.mrrockka.creator.WithdrawalsCreator.withdrawals;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class CashCalculationStrategyTest {
  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final CalculationStrategy strategy = new CashCalculationStrategy();

  private static Stream<Arguments> playerSize() {
    return Stream.of(
      Arguments.of(2),
      Arguments.of(4),
      Arguments.of(10),
      Arguments.of(20),
      Arguments.of(100)
    );
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInEquallyAndWithdrawEqually_whenCalculateExecuted_thenPayoutsListShouldContainEntryAmountAndWithdrawAmountsWithTotalZero(
    final int size) {
    final var entryAndWithdrawal = entriesWithdrawalsPairList(size);

    final var game = GameCreator.cash(builder -> builder
      .entries(entryAndWithdrawal.stream().map(Pair::getKey).toList())
      .withdrawals(entryAndWithdrawal.stream().map(Pair::getValue).toList())
    );

    final var actual = strategy.calculate(game);
    final var expected = entryAndWithdrawal.stream()
      .map(entriesWithdrawals -> Payout.builder()
        .entries(entriesWithdrawals.getKey())
        .withdrawals(entriesWithdrawals.getValue())
        .debts(emptyList())
        .build())
      .toList();

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInAndWithdrawalNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(final int size) {
    final var entryAndWithdrawal = new ArrayList<>(entriesWithdrawalsPairList(size));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN, BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN, BUY_IN, BUY_IN), emptyList()));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN), emptyList()));


    final var game = GameCreator.cash(builder -> builder
      .entries(entryAndWithdrawal.stream().map(Pair::getKey).toList())
      .withdrawals(entryAndWithdrawal.stream().map(Pair::getValue).toList())
    );

    final var actual = strategy.calculate(game);
    final var expected = new ArrayList<>(
      entryAndWithdrawal.stream()
        .filter(pair -> pair.getKey().entries().size() == 1 && pair.getValue().withdrawals().size() == 1)
        .map(entriesWithdrawals -> Payout.builder()
          .entries(entriesWithdrawals.getKey())
          .withdrawals(entriesWithdrawals.getValue())
          .debts(emptyList())
          .build())
        .toList());

    expected.add(
      payout(
        entryAndWithdrawal.get(size + 1).getKey(),
        entryAndWithdrawal.get(size + 1).getValue(),
        List.of(debt(entryAndWithdrawal.get(size + 3).getKey(), entryAndWithdrawal.get(size + 3).getValue(),
                     BUY_IN.multiply(BigDecimal.valueOf(4)))
        )));
    expected.add(
      payout(
        entryAndWithdrawal.get(size).getKey(),
        entryAndWithdrawal.get(size).getValue(),
        List.of(debt(entryAndWithdrawal.get(size + 2).getKey(), entryAndWithdrawal.get(size + 2).getValue(),
                     BUY_IN.multiply(BigDecimal.valueOf(3)))
        )));

    assertThat(actual)
      .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  void givenPlayersHaveMixedBuyInAndWithdrawal_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final List<Pair<Entries, Withdrawals>> entryAndWithdrawal = new ArrayList<>();
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN, BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN, BUY_IN), List.of(BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN)));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN, BUY_IN), emptyList()));
    entryAndWithdrawal.add(entriesWithdrawalsPair(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN), emptyList()));

    final var game = GameCreator.cash(builder -> builder
      .entries(entryAndWithdrawal.stream().map(Pair::getKey).toList())
      .withdrawals(entryAndWithdrawal.stream().map(Pair::getValue).toList())
    );

    final var actual = strategy.calculate(game);
    final var expected = List.of(
      payout(
        entryAndWithdrawal.get(1).getKey(),
        entryAndWithdrawal.get(1).getValue(),
        List.of(debt(entryAndWithdrawal.get(5).getKey(), entryAndWithdrawal.get(5).getValue(),
                     BUY_IN.multiply(BigDecimal.valueOf(4)))
        )),
      payout(
        entryAndWithdrawal.get(0).getKey(),
        entryAndWithdrawal.get(0).getValue(),
        List.of(
          debt(entryAndWithdrawal.get(4).getKey(), entryAndWithdrawal.get(4).getValue(),
               BUY_IN.multiply(BigDecimal.valueOf(2))),
          debt(entryAndWithdrawal.get(2).getKey(), entryAndWithdrawal.get(2).getValue(), BUY_IN)
        )),
      payout(
        entryAndWithdrawal.get(3).getKey(),
        entryAndWithdrawal.get(3).getValue(),
        emptyList()
      )
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  private Payout payout(final Entries entries, final Withdrawals withdrawals, final List<Debt> debts) {
    return Payout.builder()
      .entries(entries)
      .withdrawals(withdrawals)
      .debts(debts)
      .build();
  }

  private Debt debt(final Entries entries, final Withdrawals withdrawals, final BigDecimal amount) {
    return Debt.builder()
      .entries(entries)
      .withdrawals(withdrawals)
      .amount(amount)
      .build();
  }

  private List<Pair<Entries, Withdrawals>> entriesWithdrawalsPairList(final int size) {
    return IntStream.range(0, size)
      .mapToObj(i -> entriesWithdrawalsPair(List.of(BUY_IN), List.of(BUY_IN)))
      .toList();
  }

  private Pair<Entries, Withdrawals> entriesWithdrawalsPair(final List<BigDecimal> entriesAmounts,
                                                            final List<BigDecimal> withdrawalsAmount) {
    final var person = PersonCreator.domainRandom();
    return Pair.of(
      entries(builder -> builder.person(person).entries(entriesAmounts)),
      withdrawals(builder -> builder.person(person).withdrawals(withdrawalsAmount))
    );
  }

}