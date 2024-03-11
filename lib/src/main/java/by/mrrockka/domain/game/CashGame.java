package by.mrrockka.domain.game;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.withdrawals.Withdrawals;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CashGame extends Game {

  List<Withdrawals> withdrawals;

  @Builder(builderMethodName = "cashBuilder")
  public CashGame(@NonNull final UUID id, @NonNull final GameType gameType, @NonNull final BigDecimal buyIn,
                  @NonNull final BigDecimal stack, final List<Player> players, final List<Entries> entries,
                  @NonNull final List<Withdrawals> withdrawals) {
    super(id, gameType, buyIn, stack, players, entries);
    this.withdrawals = withdrawals;
  }
}
