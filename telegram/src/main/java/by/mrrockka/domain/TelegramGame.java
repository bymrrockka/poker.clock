package by.mrrockka.domain;

import by.mrrockka.domain.game.Game;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TelegramGame(
  @NonNull
  Game game,
  @NonNull
  MessageMetadata messageMetadata
) {}
