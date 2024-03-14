package by.mrrockka.features.calculation;

import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.finale.FinalePlaceSummary;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

class TournamentCalculationStrategyTest {
  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final CalculationStrategy strategy = new TournamentCalculationStrategy();

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
    final var entries = EntriesCreator.entriesList(size, BUY_IN);
    final var totalEntriesAmount = totalEntriesAmount(entries);
    final var finaleSummary = List.of(finaleSummary(entries.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .finaleSummary(new FinaleSummary(finaleSummary))
    );

    final var actual = strategy.calculate(game);
    final var expect = payouts(entries.get(0), entries);

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(final int size) {
    final var entries = new ArrayList<>(EntriesCreator.entriesList(size, BUY_IN));
    entries.add(entry(List.of(BUY_IN, BUY_IN, BUY_IN)));
    entries.add(entry(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN)));

    final var totalEntriesAmount = totalEntriesAmount(entries);
    final var finaleSummary = List.of(finaleSummary(entries.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .finaleSummary(new FinaleSummary(finaleSummary))
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
    final var entries = EntriesCreator.entriesList(size, BUY_IN);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .finaleSummary(new FinaleSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        payer(entries.get(3), BUY_IN),
        payer(entries.get(4), BUY_IN),
        payer(entries.get(5), BUY_IN),
        payer(entries.get(6), BUY_IN),
        payer(entries.get(7), BUY_IN)
      )),
      payout(entries.get(1), List.of(
        payer(entries.get(8), BUY_IN),
        payer(entries.get(9), BUY_IN)
      )),
      payout(entries.get(2), Collections.emptyList())
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final int size = 10;
    final var entries = new ArrayList<>(EntriesCreator.entriesList(size, BUY_IN));

    final var firstPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = entry(List.of(BUY_IN, BUY_IN));

    entries.set(0, firstPlace);
    entries.set(1, secondPlace);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .finaleSummary(new FinaleSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        payer(entries.get(3), BUY_IN),
        payer(entries.get(4), BUY_IN),
        payer(entries.get(5), BUY_IN),
        payer(entries.get(6), BUY_IN),
        payer(entries.get(7), BigDecimal.valueOf(16))
      )),
      payout(entries.get(1), List.of(
        payer(entries.get(8), BUY_IN),
        payer(entries.get(9), BigDecimal.valueOf(18))
      )),
      payout(entries.get(2), List.of(
        payer(entries.get(7), BigDecimal.valueOf(4)),
        payer(entries.get(9), BigDecimal.valueOf(2))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositionsAndPrizePositionStillHasDebt_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    final int size = 10;
    final var entries = new ArrayList<>(EntriesCreator.entriesList(size, BUY_IN));

    final var firstPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = entry(List.of(BUY_IN, BUY_IN));
    final var thirdPlace = entry(List.of(BUY_IN, BUY_IN, BUY_IN));

    entries.set(0, firstPlace);
    entries.set(1, secondPlace);
    entries.set(2, thirdPlace);

    final var game = GameCreator.tournament(builder -> builder
      .entries(entries)
      .finaleSummary(new FinaleSummary(finaleSummaries(entries)))
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0), List.of(
        payer(entries.get(2), BigDecimal.valueOf(30)),
        payer(entries.get(3), BUY_IN),
        payer(entries.get(4), BUY_IN),
        payer(entries.get(5), BUY_IN),
        payer(entries.get(6), BUY_IN),
        payer(entries.get(7), BigDecimal.valueOf(10))
      )),
      payout(entries.get(1), List.of(
        payer(entries.get(8), BUY_IN),
        payer(entries.get(9), BUY_IN),
        payer(entries.get(7), BigDecimal.valueOf(10))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  private PersonEntries entry(final List<BigDecimal> entries) {
    return EntriesCreator.entries(builder -> builder.entries(entries));
  }

  private FinalePlaceSummary finaleSummary(final Person person, final BigDecimal amount, final int position) {
    return FinalePlaceSummary.builder()
      .person(person)
      .position(position)
      .amount(amount)
      .build();
  }

  private List<Payout> payouts(final PersonEntries creditorEntries, final List<PersonEntries> debtorsEntries) {
    final var debts = debtorsEntries.stream()
      .filter(entries -> !entries.equals(creditorEntries))
      .map(debtEntries -> Payer.builder()
        .personEntries(debtEntries)
        .amount(debtEntries.total())
        .build())
      .toList();

    return List.of(payout(creditorEntries, debts));
  }

  private Payout payout(final PersonEntries creditorEntries, final List<Payer> payers) {
    return Payout.builder()
      .personEntries(creditorEntries)
      .payers(payers)
      .build();
  }

  private Payer payer(final PersonEntries personEntries, final BigDecimal amount) {
    return Payer.builder()
      .personEntries(personEntries)
      .amount(amount)
      .build();
  }

  private BigDecimal totalEntriesAmount(final List<PersonEntries> entriesList) {
    return entriesList.stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElseThrow();
  }

  private BigDecimal calculatePrizeAmount(final BigDecimal total, final BigDecimal percentage) {
    return total.multiply(percentage).divide(BigDecimal.valueOf(100), 0, HALF_UP);
  }

  private List<FinalePlaceSummary> finaleSummaries(final List<PersonEntries> entries) {
    final var totalEntriesAmount = totalEntriesAmount(entries);
    return List.of(
      finaleSummary(entries.get(0).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(60)), 1),
      finaleSummary(entries.get(1).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(30)), 2),
      finaleSummary(entries.get(2).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(10)), 3)
    );
  }

}