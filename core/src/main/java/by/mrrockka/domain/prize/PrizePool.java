package by.mrrockka.domain.prize;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_DOWN;

@Builder
public record PrizePool(@NonNull List<PositionAndPercentage> positionAndPercentages) {

  public BigDecimal calculatePrizeAmountFor(final int position, @NonNull final BigDecimal totalAmount) {
    if (positionAndPercentages().get(positionAndPercentages().size() - 1).position() == position) {
      final var calculatedPrizeExceptLast = positionAndPercentages().stream()
        .filter(pp -> pp.position() != position)
        .map(PositionAndPercentage::percentage)
        .map(totalPercentage -> calculatePrize(totalPercentage, totalAmount))
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);

      return totalAmount.subtract(calculatedPrizeExceptLast);
    }

    return positionAndPercentages().stream()
      .filter(positionAndPercentage -> position == positionAndPercentage.position())
      .map(pp -> calculatePrize(pp.percentage(), totalAmount))
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPositionException(position));
  }

  private BigDecimal calculatePrize(final BigDecimal percentage, final BigDecimal totalAmount) {
    return percentage.multiply(totalAmount).divide(BigDecimal.valueOf(100), 0, HALF_DOWN);
  }

  public boolean isInPrizes(final int position) {
    return positionAndPercentages().stream()
      .anyMatch(positionAndPercentage -> positionAndPercentage.position() == position);
  }
}
