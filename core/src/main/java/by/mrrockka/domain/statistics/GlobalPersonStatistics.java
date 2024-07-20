package by.mrrockka.domain.statistics;


import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record GlobalPersonStatistics(
  @NonNull
  Person person,
  @NonNull
  Integer gamesPlayed,
  @NonNull
  BigDecimal totalMoneyIn,
  @NonNull
  BigDecimal totalMoneyOut,
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
  BigDecimal outToInRatio
) {

}
