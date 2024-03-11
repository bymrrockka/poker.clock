package by.mrrockka.domain.game;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.summary.TournamentGameSummary;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TournamentGame extends Game {

  TournamentGameSummary tournamentGameSummary;

  @Builder(builderMethodName = "tournamentBuilder")
  public TournamentGame(@NonNull final UUID id, @NonNull final GameType gameType,
                        @NonNull final BigDecimal buyIn, @NonNull final BigDecimal stack,
                        final List<Player> players, final TournamentGameSummary tournamentGameSummary) {
    super(id, gameType, buyIn, stack, players);
    this.tournamentGameSummary = tournamentGameSummary;
  }
}
