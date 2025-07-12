package by.mrrockka.domain;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record TelegramGame(
  @NonNull
  Game game,
  @NonNull
  MessageMetadata messageMetadata
) {}