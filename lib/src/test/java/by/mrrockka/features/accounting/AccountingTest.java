package by.mrrockka.features.accounting;

import by.mrrockka.domain.payments.NoPaymentsException;
import by.mrrockka.domain.payments.Payments;
import by.mrrockka.domain.payout.Debt;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.player.Person;
import by.mrrockka.domain.player.Player;
import by.mrrockka.domain.prize.PercentageAndPosition;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    final var prizeAndPositions = List.of(prizeAndPosition(BigDecimal.valueOf(100), 1));

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = payouts(players.get(0), players);

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(int size) {
    final var players = new ArrayList<>(players(size));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN), size + 1));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN), size + 2));
    final var prizeAndPositions = List.of(prizeAndPosition(BigDecimal.valueOf(100), 1));

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = payouts(players.get(0), players.stream()
      .sorted((o1, o2) -> o2.payments().total().compareTo(o1.payments().total()))
      .toList());

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));
    final var prizeAndPositions = List.of(
      prizeAndPosition(BigDecimal.valueOf(60), 1),
      prizeAndPosition(BigDecimal.valueOf(30), 2),
      prizeAndPosition(BigDecimal.valueOf(10), 3)
    );

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = List.of(
      payout(getByPosition(players, 1), List.of(
        debt(getByPosition(players, 4), BUY_IN),
        debt(getByPosition(players, 5), BUY_IN),
        debt(getByPosition(players, 6), BUY_IN),
        debt(getByPosition(players, 7), BUY_IN),
        debt(getByPosition(players, 8), BUY_IN)
      )),
      payout(getByPosition(players, 2), List.of(
        debt(getByPosition(players, 9), BUY_IN),
        debt(getByPosition(players, 10), BUY_IN)
      )),
      payout(getByPosition(players, 3), Collections.emptyList())
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));

    final var firstPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN), 0);
    final var secondPlace = player(List.of(BUY_IN, BUY_IN), 1);

    players.set(0, firstPlace);
    players.set(1, secondPlace);

    final var prizeAndPositions = List.of(
      prizeAndPosition(BigDecimal.valueOf(60), 1),
      prizeAndPosition(BigDecimal.valueOf(30), 2),
      prizeAndPosition(BigDecimal.valueOf(10), 3)
    );

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = List.of(
      payout(getByPosition(players, 1), List.of(
        debt(getByPosition(players, 4), BUY_IN),
        debt(getByPosition(players, 5), BUY_IN),
        debt(getByPosition(players, 6), BUY_IN),
        debt(getByPosition(players, 7), BUY_IN),
        debt(getByPosition(players, 8), BigDecimal.valueOf(16))
      )),
      payout(getByPosition(players, 2), List.of(
        debt(getByPosition(players, 9), BUY_IN),
        debt(getByPosition(players, 10), BigDecimal.valueOf(18))
      )),
      payout(getByPosition(players, 3), List.of(
        debt(getByPosition(players, 8), BigDecimal.valueOf(4)),
        debt(getByPosition(players, 10), BigDecimal.valueOf(2))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositionsAndPrizePositionStillHasDebt_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));

    final var firstPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN), 0);
    final var secondPlace = player(List.of(BUY_IN, BUY_IN), 1);
    final var thirdPlace = player(List.of(BUY_IN, BUY_IN, BUY_IN), 2);

    players.set(0, firstPlace);
    players.set(1, secondPlace);
    players.set(2, thirdPlace);

    final var prizeAndPositions = List.of(
      prizeAndPosition(BigDecimal.valueOf(60), 1),
      prizeAndPosition(BigDecimal.valueOf(30), 2),
      prizeAndPosition(BigDecimal.valueOf(10), 3)
    );

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = List.of(
      payout(getByPosition(players, 1), List.of(
        debt(getByPosition(players, 3), BigDecimal.valueOf(30)),
        debt(getByPosition(players, 4), BUY_IN),
        debt(getByPosition(players, 5), BUY_IN),
        debt(getByPosition(players, 6), BUY_IN),
        debt(getByPosition(players, 7), BUY_IN),
        debt(getByPosition(players, 8), BigDecimal.valueOf(10))
      )),
      payout(getByPosition(players, 2), List.of(
        debt(getByPosition(players, 9), BUY_IN),
        debt(getByPosition(players, 10), BUY_IN),
        debt(getByPosition(players, 8), BigDecimal.valueOf(10))
      ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayersPositionOverlap_thenShouldCreateListOfDebtorsRelatedToCreditorWithNoDuplicates() {
    final var players = new ArrayList<>(players(2));
    players.add(player(List.of(BUY_IN, BUY_IN), 0));

    final var prizeAndPositions = List.of(prizeAndPosition(BigDecimal.valueOf(100), 1));

    final var actual = accounting.calculate(players, prizeAndPositions);
    final var expect = payouts(players.get(0), players(2));

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }


  @Test
  void givenPlayersAndOnePlayerDoesntHavePayments_thenShouldThrowException() {
    final var players = new ArrayList<>(players(2));
    players.add(player(null, 2));

    final var prizeAndPositions = List.of(prizeAndPosition(BigDecimal.valueOf(100), 1));

    assertThatThrownBy(() -> accounting.calculate(players, prizeAndPositions))
      .isInstanceOf(NoPaymentsException.class);
  }


  private List<Player> players(int size) {
    return IntStream.range(0, size)
      .mapToObj(this::player)
      .toList();
  }

  private Player player(int position) {
    return player(List.of(BUY_IN), position);
  }

  private Player player(final List<BigDecimal> entries, int position) {
    return Player.builder()
      .payments(Payments.builder()
        .entries(entries)
        .build())
      .position(position + 1)
      .person(Person.builder().build())
      .build();
  }

  private PercentageAndPosition prizeAndPosition(BigDecimal prize, int position) {
    return PercentageAndPosition.builder()
      .prize(prize)
      .place(position)
      .build();
  }

  private List<Payout> payouts(Player creditor, List<Player> debtors) {
    final var debts = debtors.stream()
      .filter(player -> !player.equals(creditor))
      .map(player -> Debt.builder()
        .debtor(player)
        .amount(player.payments().total())
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

  private Player getByPosition(List<Player> players, int position) {
    return players.stream()
      .filter(player -> player.position() == position)
      .findFirst()
      .orElseThrow();
  }

  private Debt debt(Player debtor, BigDecimal amount) {
    return Debt.builder()
      .debtor(debtor)
      .amount(amount)
      .build();
  }

}