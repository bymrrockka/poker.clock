package by.mrrockka.repo.prizepool;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record PrizePoolEntity(
  UUID gameId,
  Map<Integer, BigDecimal> schema
) {
}
