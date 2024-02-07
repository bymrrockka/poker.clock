package by.mrrockka.repo.bounty;

import by.mrrockka.repo.person.PersonEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record BountyEntity(
  UUID gameId,
  PersonEntity fromEntity,
  PersonEntity toEntity,
  BigDecimal amount
) {
}
