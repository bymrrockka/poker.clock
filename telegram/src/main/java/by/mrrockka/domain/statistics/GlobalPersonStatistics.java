package by.mrrockka.domain.statistics;


import by.mrrockka.domain.Person;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

//todo: move to main lib
public record GlobalPersonStatistics(
  Person person,
  int gamesPlayed,
  BigDecimal totalMoneyIn,
  BigDecimal totalMoneyWon,
  BigDecimal totalMoneyLose,
  int timesOnFirstPlace,
  int timesInPrizes
) {

  public BigDecimal inPrizeRatio() {
    return leftToRightRatio(BigDecimal.valueOf(timesInPrizes), BigDecimal.valueOf(gamesPlayed));
  }

  public BigDecimal wonToLoseRatio() {
    return leftToRightRatio(totalMoneyWon, totalMoneyLose);
  }

  private BigDecimal leftToRightRatio(final @NonNull BigDecimal left, final @NonNull BigDecimal right) {
    return left.divide(right, 2, RoundingMode.DOWN)
      .multiply(BigDecimal.valueOf(100));
  }
}
