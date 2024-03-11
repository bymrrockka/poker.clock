package by.mrrockka.domain.game;

import by.mrrockka.domain.entries.Entries;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract sealed class Game permits TournamentGame, CashGame, BountyGame {

  @NonNull
  protected UUID id;
  @NonNull
  protected BigDecimal buyIn;
  @NonNull
  protected BigDecimal stack;
  protected List<Entries> entries;

  protected Game(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                 @NonNull final BigDecimal stack, final List<Entries> entries) {
    this.id = id;
    this.buyIn = buyIn;
    this.stack = stack;
    this.entries = entries;
  }
}
