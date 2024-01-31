package by.mrrockka.repo.entities;

import java.math.BigDecimal;
import java.util.UUID;

public record BountyEntity(
  UUID gameId,
  PersonEntity from,
  PersonEntity to,
  BigDecimal amount
) {
}
