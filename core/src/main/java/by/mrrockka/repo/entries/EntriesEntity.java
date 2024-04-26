package by.mrrockka.repo.entries;

import by.mrrockka.repo.person.PersonEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record EntriesEntity(
  UUID gameId,
  PersonEntity person,
  List<BigDecimal> amounts
) {
}
