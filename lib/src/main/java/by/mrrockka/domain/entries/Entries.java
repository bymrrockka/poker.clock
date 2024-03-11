package by.mrrockka.domain.entries;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

//todo: probably entries should contain person data and player model is not needed
@Builder
public record Entries(@NonNull Person person, @NonNull List<BigDecimal> entries) {

  public BigDecimal total() {
    return entries()
      .stream()
      .reduce(BigDecimal::add)
      .orElseThrow(NoEntriesException::new);
  }

}