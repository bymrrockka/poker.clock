package by.mrrockka.domain;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TelegramGame<T extends Game>(
  @NonNull
  T game,
  @NonNull
  MessageMetadata messageMetadata
) {
  public TournamentGame gameAsTournament() {
    return (TournamentGame) game;
  }
}
