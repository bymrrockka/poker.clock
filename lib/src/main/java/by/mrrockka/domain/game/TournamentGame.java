package by.mrrockka.domain.game;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TournamentGame extends Game {

  FinaleSummary finaleSummary;

  @Builder(builderMethodName = "tournamentBuilder")
  public TournamentGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                        @NonNull final BigDecimal stack, final List<PersonEntries> entries,
                        final FinaleSummary finaleSummary) {
    super(id, buyIn, stack, entries);
    this.finaleSummary = finaleSummary;
  }
}
