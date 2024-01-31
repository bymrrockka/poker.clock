package by.mrrockka.repo.entities;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record PrizePoolEntity(
  UUID gameId,
  Map<Integer, BigDecimal> schema
) {
}
