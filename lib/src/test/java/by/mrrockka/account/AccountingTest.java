package by.mrrockka.account;

import by.mrrockka.model.*;
import lombok.Builder;
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

class AccountingTest {


  /* Scenarios:
   *  1. all player buy in equally and prize pull has only first position
   *  - should calculate total money pot
   *  - should create list of debtors related to creditor
   *
   *  2. two players did re-entries and prize pull has only first position and they payments dispersed by largest
   *  - should calculate total money pot
   *  - should create list of debtors related to creditor
   *  - should calculate players with larger debts and make them debtors of the larger prize
   *
   *  3. two players buy in equally and prize pull has multiple positions and they payments dispersed by largest
   *  - should calculate total money pot
   *  - should create list of debtors related to creditor
   *  - should calculate players with larger debts and make them debtors of the larger prize
   *
   * ? 4. two players did re-entries and they payments dispersed by largest
   *  - should calculate total money pot
   *  - should create list of debtors related to creditor
   *  - should calculate players with larger debts and make them debtors of the larger prize
   *
   *  5. two players did re-entries and they payments split from first to last
   *  - should calculate total money pot
   *  - should create list of debtors related to creditor
   *  - should calculate players with larger debts and make them debtors of the larger prize
   *  - if someone's debt is not fit to prize amount then should be split
   *
   * */

  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final Accounting accounting = new Accounting();

  @Builder
  private record AccountingArg(int playersSize) {
  }

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
    final var prizePool = prizePool();

    final var actual = accounting.calculate(players, prizePool);
    final var expect = payouts(players.get(0), players);

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @ParameterizedTest
  @MethodSource("playerSize")
  void givenPlayerBuyInNotEqually_thenShouldCreateListOfDebtorsOrderedByTheAmount(int size) {
    final var players = new ArrayList<>(players(size));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN), size + 1));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN), size + 2));
    final var prizePool = prizePool();

    final var actual = accounting.calculate(players, prizePool);
    final var expect = payouts(players.get(0), players.stream().sorted().toList());

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndPrizePoolHasMutiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));
    final var prizePool = prizePool(List.of(
      prizeAndPosition(BigDecimal.valueOf(60), 1),
      prizeAndPosition(BigDecimal.valueOf(30), 2),
      prizeAndPosition(BigDecimal.valueOf(10), 3)
    ));

    final var actual = accounting.calculate(players, prizePool);
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
  void givenPlayerBuyInNotEquallyAndPrizePoolHasMutiplePositions_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
    int size = 10;
    final var players = new ArrayList<>(players(size));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN), size + 1));
    players.add(player(List.of(BUY_IN, BUY_IN, BUY_IN, BUY_IN), size + 2));
    final var prizePool = prizePool();

    final var actual = accounting.calculate(players, prizePool);
    final var expect = payouts(players.get(0), players.stream().sorted().toList());

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
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

  private PrizePool prizePool() {
    final var prizes = List.of(prizeAndPosition(BigDecimal.valueOf(100), 1));

    return prizePool(prizes);
  }

  private PrizePool prizePool(List<PrizeAndPosition> prizeAndPositionList) {
    return PrizePool.builder()
      .prizeAndPositionList(prizeAndPositionList)
      .build();
  }

  private PrizeAndPosition prizeAndPosition(BigDecimal prize, int position) {
    return PrizeAndPosition.builder()
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