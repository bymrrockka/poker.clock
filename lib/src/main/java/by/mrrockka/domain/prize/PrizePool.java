package by.mrrockka.domain.prize;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Builder
public record PrizePool(@NonNull List<PercentageAndPosition> percentageAndPositions,
                        @NonNull BigDecimal totalBuyInsAmount) {

  public BigDecimal getPrizeFor(int position) {
    return percentageAndPositions().stream()
      .filter(percentageAndPosition -> position == percentageAndPosition.position())
      .map(percentageAndPosition -> percentageAndPosition.percentage()
                                                         .multiply(totalBuyInsAmount)
                                                         .divide(BigDecimal.valueOf(100), 0, HALF_UP))
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPositionException(position));
  }

  public boolean isInPrizes(int position) {
    return percentageAndPositions().stream()
      .anyMatch(percentageAndPosition -> percentageAndPosition.position() == position);
  }

}
