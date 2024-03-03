package by.mrrockka.features.accounting;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.payments.Entries;
import by.mrrockka.domain.payments.NoEntriesException;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.FinalePlaceSummary;
import by.mrrockka.domain.summary.GameSummary;
import org.assertj.core.api.Assertions;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountingTest {
  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final Accounting accounting = new Accounting();

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
  void givenPlayerBuyInEqually_thenShouldCreateListOfDebtorsRelatedToCreditor(int size) {
    final var players = players(size);
    final var totalEntriesAmount = totalEntriesAmount(players);
    final var finaleSummary = List.of(finaleSummary(players.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummary))
    );

    final var actual = accounting.calculate(game);
    final var expect = payouts(players.get(0), players);

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(int size) {
    final var players = new ArrayList<>(players(size));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN)));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN)));

    final var totalEntriesAmount = totalEntriesAmount(players);
    final var finaleSummary = List.of(finaleSummary(players.get(0).person(), totalEntriesAmount, 1));
    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummary))
    );

    final var actual = accounting.calculate(game);
    final var expect = payouts(players.get(0), players.stream()
      .sorted((o1, o2) -> o2.entries().total().compareTo(o1.entries().total()))
      .toList());

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = players(size);

    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummaries(players)))
    );

    final var actual = accounting.calculate(game);
    final var expect = List.of(
      payout(players.get(0), List.of(
        debt(players.get(3), BUY_IN),
        debt(players.get(4), BUY_IN),
        debt(players.get(5), BUY_IN),
        debt(players.get(6), BUY_IN),
        debt(players.get(7), BUY_IN)
      )),
      payout(players.get(1), List.of(
        debt(players.get(8), BUY_IN),
        debt(players.get(9), BUY_IN)
      )),
      payout(players.get(2), Collections.emptyList())
    );

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));

    final var firstPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = player(List.of(BUY_IN, BUY_IN));

    players.set(0, firstPlace);
    players.set(1, secondPlace);

    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummaries(players)))
    );

    final var actual = accounting.calculate(game);
    final var expect = List.of(
      payout(players.get(0), List.of(
        debt(players.get(3), BUY_IN),
        debt(players.get(4), BUY_IN),
        debt(players.get(5), BUY_IN),
        debt(players.get(6), BUY_IN),
        debt(players.get(7), BigDecimal.valueOf(16))
      )),
      payout(players.get(1), List.of(
        debt(players.get(8), BUY_IN),
        debt(players.get(9), BigDecimal.valueOf(18))
      )),
      payout(players.get(2), List.of(
        debt(players.get(7), BigDecimal.valueOf(4)),
        debt(players.get(9), BigDecimal.valueOf(2))
      ))
    );

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositionsAndPrizePositionStillHasDebt_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));

    final var firstPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN));
    final var secondPlace = player(List.of(BUY_IN, BUY_IN));
    final var thirdPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN));

    players.set(0, firstPlace);
    players.set(1, secondPlace);
    players.set(2, thirdPlace);

    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummaries(players)))
    );

    final var actual = accounting.calculate(game);
    final var expect = List.of(
      payout(players.get(0), List.of(
        debt(players.get(2), BigDecimal.valueOf(30)),
        debt(players.get(3), BUY_IN),
        debt(players.get(4), BUY_IN),
        debt(players.get(5), BUY_IN),
        debt(players.get(6), BUY_IN),
        debt(players.get(7), BigDecimal.valueOf(10))
      )),
      payout(players.get(1), List.of(
        debt(players.get(8), BUY_IN),
        debt(players.get(9), BUY_IN),
        debt(players.get(7), BigDecimal.valueOf(10))
      ))
    );

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayersAndOnePlayerDoesNotHavePayments_thenShouldThrowException() {
    final var players = new ArrayList<>(players(2));
    players.add(player(null));

    final var finaleSummary = List.of(finaleSummary(players.get(0).person(), BUY_IN, 1));
    final var game = GameCreator.domain(builder -> builder
      .players(players)
      .gameSummary(new GameSummary(finaleSummary))
    );

    assertThatThrownBy(() -> accounting.calculate(game))
      .isInstanceOf(NoEntriesException.class);
  }


  private List<Player> players(int size) {
    return IntStream.range(0, size)
      .mapToObj(i -> player())
      .toList();
  }

  private Player player() {
    return player(List.of(BUY_IN));
  }

  private Player player(final List<BigDecimal> entries) {
    return Player.builder()
      .entries(Entries.builder()
                 .entries(entries)
                 .build())
      .person(PersonCreator.domainRandom())
      .build();
  }

  private FinalePlaceSummary finaleSummary(Person person, BigDecimal amount, int position) {
    return FinalePlaceSummary.builder()
      .person(person)
      .position(position)
      .amount(amount)
      .build();
  }

  private List<Payout> payouts(Player creditor, List<Player> debtors) {
    final var debts = debtors.stream()
      .filter(player -> !player.equals(creditor))
      .map(player -> Debt.builder()
        .debtor(player)
        .amount(player.entries().total())
        .build())
      .toList();

    return List.of(payout(creditor, debts));
  }

  private Payout payout(Player creditor, List<Debt> debts) {
    return Payout.builder()
      .creditor(creditor)
      .debts(debts)
      .build();
  }

  private Debt debt(Player debtor, BigDecimal amount) {
    return Debt.builder()
      .debtor(debtor)
      .amount(amount)
      .build();
  }

  private BigDecimal totalEntriesAmount(final List<Player> players) {
    return players.stream()
      .map(player -> player.entries().total())
      .reduce(BigDecimal::add)
      .orElseThrow();
  }

  private BigDecimal calculatePrizeAmount(BigDecimal total, BigDecimal percentage) {
    return total.multiply(percentage).divide(BigDecimal.valueOf(100), 0, HALF_UP);
  }

  private List<FinalePlaceSummary> finaleSummaries(List<Player> players) {
    final var totalEntriesAmount = totalEntriesAmount(players);
    return List.of(
      finaleSummary(players.get(0).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(60)), 1),
      finaleSummary(players.get(1).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(30)), 2),
      finaleSummary(players.get(2).person(), calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(10)), 3)
    );
  }

}