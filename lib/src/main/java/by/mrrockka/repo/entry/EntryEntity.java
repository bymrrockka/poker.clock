package by.mrrockka.repo.entry;

import by.mrrockka.domain.Person;

import java.math.BigDecimal;
import java.util.UUID;

public record EntryEntity(
  UUID gameId,
  Person person,
  BigDecimal amount
) {
}
