package by.mrrockka.domain.statistics;


import java.math.BigDecimal;

public record GlobalPersonStatistics(
  String nickname,
  int gamesPlayed,
  BigDecimal moneyIn,
  BigDecimal totalMoneyOut,
  int timesOnFirstPlace,
  int timesInPrizes,
  BigDecimal inPrizeRatio
) {
}
