package by.mrrockka.domain;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Withdrawals(@NonNull Person person, @NonNull List<BigDecimal> withdrawals) {

  public BigDecimal total() {
    return withdrawals()
      .stream()
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }
}
