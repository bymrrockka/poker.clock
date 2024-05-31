package by.mrrockka.domain.statistics;


import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Builder
public record GlobalPersonStatistics(
  @NonNull
  Person person,
  @NonNull
  Integer gamesPlayed,
  @NonNull
  BigDecimal totalMoneyIn,
  @NonNull
  BigDecimal totalMoneyWon,
  @NonNull
  BigDecimal totalMoneyLose,
  @NonNull
  Integer timesOnFirstPlace,
  @NonNull
  Integer timesInPrizes,
  @NonNull
  BigDecimal inPrizeRatio,
  @NonNull
  BigDecimal wonToLoseRatio
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
