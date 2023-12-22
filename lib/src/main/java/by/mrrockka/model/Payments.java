package by.mrrockka.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Payments(List<BigDecimal> entries) {

  public BigDecimal total() {
    return entries().stream()
      .reduce(BigDecimal::add)
      .orElseThrow(() -> new NullPointerException("No entries for player"));
  }

}
