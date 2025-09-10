package by.mrrockka.domain.collection;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Deprecated(forRemoval = true)
public record PersonEntries(@NonNull Person person, @NonNull List<BigDecimal> entries) {

  public BigDecimal total() {
    return entries()
      .stream()
      .reduce(BigDecimal::add)
      .orElseThrow(NoEntriesException::new);
  }
}
