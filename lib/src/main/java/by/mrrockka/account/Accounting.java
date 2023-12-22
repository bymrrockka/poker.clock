package by.mrrockka.account;

import by.mrrockka.model.Debt;
import by.mrrockka.model.Payout;
import by.mrrockka.model.Player;
import by.mrrockka.model.PrizePool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Accounting {


  public List<Payout> calculate(final List<Player> players, final PrizePool prizePool) {
    final var totalEntries = totalEntriesAmount(players);
    var payouts = new ArrayList<Payout>();
    var position = 1;
    var creditor = getByPosition(players, position).orElseThrow(() -> new RuntimeException("Players list is empty"));
    var debtors = players.stream()
      .filter(player -> !player.equals(creditor))
      .sorted()
      .toList();

    do {

      var debts = new ArrayList<Debt>();

      var prizeAmountOpt = prizePool.getPrizeFor(position, totalEntries);

      if (prizeAmountOpt.isEmpty()) break;

      var prizeAmount = prizeAmountOpt.get();

      for (final Player debtor : debtors) {
        final var debtAmount = debtor.payments().total();
        final var debt = Debt.builder()
          .debtor(debtor)
          .amount(debtAmount)
          .build();

        final var debtComparison = debtAmount.compareTo(prizeAmount);

        if (debtComparison == 0) {
          debts.add(debt);
          break;
        }

        if (debtComparison < 0) {
          debts.add(debt);
          prizeAmount = prizeAmount.subtract(debtAmount);
        }

        if (debtComparison > 0) {
          debts.add(debt.withAmount(debtAmount.subtract(prizeAmount)));
//          add debtor as one of not fulfiled
          break;
        }

      }

      payouts.add(
        Payout.builder()
          .debts(debts)
          .creditor(creditor)
          .build()
      );

      debtors = debtors.stream()
        .filter(player -> !debtsContains(debts, player))
        .sorted()
        .toList();


    } while (prizePool.prizeAndPositionList().size() > position++);


    return payouts;
  }

  private BigDecimal totalEntriesAmount(final List<Player> players) {
    return players.stream()
      .map(player -> player.payments().total())
      .reduce(BigDecimal::add)
      .orElseThrow(() -> new NullPointerException("No payments for player"));
  }

  private Optional<Player> getByPosition(final List<Player> players, int position) {
    return players.stream()
      .filter(player -> player.position() == position)
      .findFirst();
  }

  private boolean debtsContains(List<Debt> debts, Player player) {
    return debts.stream()
      .map(Debt::debtor)
      .anyMatch(debtor -> debtor.equals(player));
  }

}
