package by.mrrockka.repo.entities;

import by.mrrockka.domain.game.GameType;

import java.math.BigDecimal;
import java.util.UUID;

public record GameEntity(
  UUID id,
  String chatId,
  GameType gameType,
  BigDecimal buyIn,
  BigDecimal stack,
  BigDecimal bounty
) {
}
