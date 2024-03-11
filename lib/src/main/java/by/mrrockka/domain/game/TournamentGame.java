package by.mrrockka.domain.game;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.summary.TournamentSummary;
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

  TournamentSummary tournamentSummary;

  @Builder(builderMethodName = "tournamentBuilder")
  public TournamentGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                        @NonNull final BigDecimal stack, final List<Entries> entries,
                        final TournamentSummary tournamentSummary) {
    super(id, buyIn, stack, entries);
    this.tournamentSummary = tournamentSummary;
  }
}
