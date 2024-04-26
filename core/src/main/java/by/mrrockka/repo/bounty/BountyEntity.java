package by.mrrockka.repo.bounty;

import by.mrrockka.repo.person.PersonEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BountyEntity(
  UUID gameId,
  PersonEntity from,
  PersonEntity to,
  BigDecimal amount
) {
}
