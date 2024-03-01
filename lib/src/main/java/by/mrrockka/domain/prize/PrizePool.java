package by.mrrockka.domain.prize;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Builder
public record PrizePool(@NonNull List<PositionAndPercentage> positionAndPercentages) {

  public BigDecimal calculatePrizeAmountFor(int position, @NonNull BigDecimal totalBuyInsAmount) {
    return positionAndPercentages().stream()
      .filter(positionAndPercentage -> position == positionAndPercentage.position())
      .map(positionAndPercentage -> positionAndPercentage
        .percentage()
        .multiply(totalBuyInsAmount)
        .divide(BigDecimal.valueOf(100), 0, HALF_UP))
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPositionException(position));
  }

  public boolean isInPrizes(int position) {
    return positionAndPercentages().stream()
      .anyMatch(positionAndPercentage -> positionAndPercentage.position() == position);
  }

  @Override
  public String toString() {
    return """
      Prize Pool:
      %s
      """.formatted(
      positionAndPercentages().stream()
        .map(pp -> "\tposition: %s, percentage: %s".formatted(pp.position(), pp.percentage()))
        .reduce("%s\n%s"::formatted)
        .orElse(StringUtils.EMPTY)
    );
  }
}
