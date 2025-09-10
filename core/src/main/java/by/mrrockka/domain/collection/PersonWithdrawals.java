package by.mrrockka.domain.collection;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Deprecated(forRemoval = true)
public record PersonWithdrawals(@NonNull Person person, @NonNull List<BigDecimal> withdrawals) {

  public BigDecimal total() {
    return withdrawals()
      .stream()
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }
}
