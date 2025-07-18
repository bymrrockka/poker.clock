package by.mrrockka.repo.game;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Deprecated(forRemoval = true)
public record TelegramGameEntity(
  UUID gameId,
  Long chatId,
  Instant createdAt,
  Integer messageId
) {}
