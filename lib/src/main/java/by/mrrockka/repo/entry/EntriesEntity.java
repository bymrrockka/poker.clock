package by.mrrockka.repo.entry;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record EntriesEntity(
  UUID gameId,
  UUID personId,
  List<BigDecimal> amounts
) {
}
