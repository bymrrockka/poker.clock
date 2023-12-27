package by.mrrockka.model.prize;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;

@Builder
public record PrizePool(List<PrizeAndPosition> prizeAndPositionList, BigDecimal totalBuyInsAmount) {

  public BigDecimal getPrizeFor(int position) {
    validate();
    return prizeAndPositionList().stream()
      .filter(prizeAndPosition -> position == prizeAndPosition.place())
      .map(prizeAndPosition -> prizeAndPosition.prize()
        .multiply(totalBuyInsAmount)
        .divide(BigDecimal.valueOf(100), 0, HALF_UP))
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPositionException(position));
  }

  public boolean isInPrizes(int position) {
    validate();
    return prizeAndPositionList().stream()
      .anyMatch(prizeAndPosition -> prizeAndPosition.place() == position);
  }

  private void validate() {
    Objects.requireNonNull(prizeAndPositionList(), "Prize and Position list cannot be null");
    Objects.requireNonNull(totalBuyInsAmount(), "Total Buy ins cannot be null");
  }
}
