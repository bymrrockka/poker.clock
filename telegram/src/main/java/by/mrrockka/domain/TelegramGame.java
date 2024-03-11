package by.mrrockka.domain;

import by.mrrockka.domain.game.TournamentGame;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TelegramGame(
  @NonNull
  TournamentGame game,
  @NonNull
  MessageMetadata messageMetadata
) {}
