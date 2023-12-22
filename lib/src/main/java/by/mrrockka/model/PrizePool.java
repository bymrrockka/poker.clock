package by.mrrockka.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.math.RoundingMode.HALF_UP;

@Builder
public record PrizePool(List<PrizeAndPosition> prizeAndPositionList) {

  public Optional<BigDecimal> getPrizeFor(int position, BigDecimal totalBuyInsAmount) {
    validate();
    return prizeAndPositionList().stream()
      .filter(prizeAndPosition -> position == prizeAndPosition.place())
      .map(prizeAndPosition -> prizeAndPosition.prize()
        .multiply(totalBuyInsAmount)
        .divide(BigDecimal.valueOf(100), 0, HALF_UP))
      .findFirst();
  }

  private void validate() {
    Objects.requireNonNull(prizeAndPositionList(), "Prize and Position list cannot be null");
//    Objects.requireNonNull(totalBuyInsAmount(), "Total Buy ins cannot be null");

  }
}
