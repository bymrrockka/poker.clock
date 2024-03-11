package by.mrrockka.features.accounting;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.FinalePlaceSummary;
import by.mrrockka.domain.summary.TournamentGameSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

class TournamentCalculationStrategyTest {
  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final TournamentCalculationStrategy strategy = new TournamentCalculationStrategy();

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
  void givenPlayerBuyInEqually_thenShouldCreateListOfDebtorsRelatedToCreditor(final int size) {
    final var entries = entries(size);
    final var totalEntriesAmount = totalEntriesAmount(entries);
    final var finaleSummary = List.of(finaleSummary(entries.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .tournamentGameSummary(new TournamentGameSummary(finaleSummary))
    );

    final var actual = strategy.calculate(game);
    final var expect = payouts(entries.get(0), entries);

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(final int size) {
    final var entries = new ArrayList<>(entries(size));
    entries.add(entry(List.of(BUY_IN, BUY_IN, BUY_IN)));
    entries.add(entry(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN)));

    final var totalEntriesAmount = totalEntriesAmount(entries);
    final var finaleSummary = List.of(finaleSummary(entries.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .tournamentGameSummary(new TournamentGameSummary(finaleSummary))
    );

    final var actual = strategy.calculate(game);
    final var expect = payouts(entries.get(0), entries.stream()
      .sorted((o1, o2) -> o2.total().compareTo(o1.total()))
      .toList());

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final int size = 10;
    final var entries = entries(size);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .tournamentGameSummary(new TournamentGameSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        debt(entries.get(3), BUY_IN),
        debt(entries.get(4), BUY_IN),
        debt(entries.get(5), BUY_IN),
        debt(entries.get(6), BUY_IN),
        debt(entries.get(7), BUY_IN)
      )),
      payout(entries.get(1), List.of(
        debt(entries.get(8), BUY_IN),
        debt(entries.get(9), BUY_IN)
      )),
      payout(entries.get(2), Collections.emptyList())
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final int size = 10;
    final var entries = new ArrayList<>(entries(size));

    final var firstPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = entry(List.of(BUY_IN, BUY_IN));

    entries.set(0, firstPlace);
    entries.set(1, secondPlace);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .tournamentGameSummary(new TournamentGameSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        debt(entries.get(3), BUY_IN),
        debt(entries.get(4), BUY_IN),
        debt(entries.get(5), BUY_IN),
        debt(entries.get(6), BUY_IN),
        debt(entries.get(7), BigDecimal.valueOf(16))
      )),
      payout(entries.get(1), List.of(
        debt(entries.get(8), BUY_IN),
        debt(entries.get(9), BigDecimal.valueOf(18))
      )),
      payout(entries.get(2), List.of(
        debt(entries.get(7), BigDecimal.valueOf(4)),
        debt(entries.get(9), BigDecimal.valueOf(2))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositionsAndPrizePositionStillHasDebt_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final int size = 10;
    final var entries = new ArrayList<>(entries(size));

    final var firstPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = entry(List.of(BUY_IN, BUY_IN));
    final var thirdPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));

    entries.set(0, firstPlace);
    entries.set(1, secondPlace);
    entries.set(2, thirdPlace);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .tournamentGameSummary(new TournamentGameSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        debt(entries.get(2), BigDecimal.valueOf(30)),
        debt(entries.get(3), BUY_IN),
        debt(entries.get(4), BUY_IN),
        debt(entries.get(5), BUY_IN),
        debt(entries.get(6), BUY_IN),
        debt(entries.get(7), BigDecimal.valueOf(10))
      )),
      payout(entries.get(1), List.of(
        debt(entries.get(8), BUY_IN),
        debt(entries.get(9), BUY_IN),
        debt(entries.get(7), BigDecimal.valueOf(10))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  private List<Entries> entries(final int size) {
    return IntStream.range(0, size)
      .mapToObj(i -> entry())
      .toList();
  }

  private Entries entry() {
    return entry(List.of(BUY_IN));
  }

  private Entries entry(final List<BigDecimal> entries) {
    return Entries.builder()
      .person(PersonCreator.domainRandom())
      .entries(entries)
      .build();
  }

  private FinalePlaceSummary finaleSummary(final Person person, final BigDecimal amount, final int position) {
    return FinalePlaceSummary.builder()
      .person(person)
      .position(position)
      .amount(amount)
      .build();
  }

  private List<Payout> payouts(final Entries creditorEntries, final List<Entries> debtorsEntries) {
    final var debts = debtorsEntries.stream()
      .filter(entries -> !entries.equals(creditorEntries))
      .map(debtEntries -> Debt.builder()
        .debtorEntries(debtEntries)
        .amount(debtEntries.total())
        .build())
      .toList();

    return List.of(payout(creditorEntries, debts));
  }

  private Payout payout(final Entries creditorEntries, final List<Debt> debts) {
    return Payout.builder()
      .creditorEntries(creditorEntries)
      .debts(debts)
      .build();
  }

  private Debt debt(final Entries entries, final BigDecimal amount) {
    return Debt.builder()
      .debtorEntries(entries)
      .amount(amount)
      .build();
  }

  private BigDecimal totalEntriesAmount(final List<Entries> entriesList) {
    return entriesList.stream()
      .map(Entries::total)
      .reduce(BigDecimal::add)
      .orElseThrow();
  }

  private BigDecimal calculatePrizeAmount(final BigDecimal total, final BigDecimal percentage) {
    return total.multiply(percentage).divide(BigDecimal.valueOf(100), 0, HALF_UP);
  }

  private List<FinalePlaceSummary> finaleSummaries(final List<Entries> entries) {
    final var totalEntriesAmount = totalEntriesAmount(entries);
    return List.of(
      finaleSummary(entries.get(0).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(60)), 1),
      finaleSummary(entries.get(1).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(30)), 2),
      finaleSummary(entries.get(2).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(10)), 3)
    );
  }

}