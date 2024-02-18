package by.mrrockka.repo.game;

import by.mrrockka.domain.game.GameType;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GameEntity(
  @NonNull
  UUID id,
  @NonNull
  GameType gameType,
  @NonNull
  BigDecimal buyIn,
  @NonNull
  BigDecimal stack,
  BigDecimal bounty
) {
}
