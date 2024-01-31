package by.mrrockka.domain.payments;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
public record Payments(List<BigDecimal> entries) {

  public BigDecimal total() {
    return Optional.ofNullable(entries())
      .orElse(Collections.emptyList())
      .stream()
      .reduce(BigDecimal::add)
      .orElseThrow(NoPaymentsException::new);
  }

}
