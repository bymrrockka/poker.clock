package by.mrrockka.domain.game;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CashGame extends Game {

  List<PersonWithdrawals> withdrawals;

  @Builder(builderMethodName = "cashBuilder")
  public CashGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                  @NonNull final BigDecimal stack, final List<PersonEntries> entries,
                  final Instant finishedAt, final List<PersonWithdrawals> withdrawals) {
    super(id, buyIn, stack, finishedAt, entries);
    this.withdrawals = withdrawals;
  }
}
