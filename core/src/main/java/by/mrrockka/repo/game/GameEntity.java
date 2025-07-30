package by.mrrockka.repo.game;

import by.mrrockka.domain.GameType;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record GameEntity(
  @NonNull
  UUID id,
  @NonNull
  GameType gameType,
  @NonNull
  BigDecimal buyIn,
  BigDecimal stack,
  BigDecimal bounty,

  Instant finishedAt
) {
}
