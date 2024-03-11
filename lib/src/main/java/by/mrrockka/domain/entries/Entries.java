package by.mrrockka.domain.entries;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Entries(@NonNull Person person, @NonNull List<BigDecimal> entries) {

  public BigDecimal total() {
    return entries()
      .stream()
      .reduce(BigDecimal::add)
      .orElseThrow(NoEntriesException::new);
  }

}
