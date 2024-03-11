package by.mrrockka.domain.game;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
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
  public CashGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                  @NonNull final BigDecimal stack, @NonNull final List<Entries> entries,
                  @NonNull final List<Withdrawals> withdrawals) {
    super(id, buyIn, stack, entries);
    this.withdrawals = withdrawals;
  }
}
