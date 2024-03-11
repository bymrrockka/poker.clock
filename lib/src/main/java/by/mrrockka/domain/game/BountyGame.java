package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
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
public final class BountyGame extends Game {

  @NonNull
  BigDecimal bounty;
  List<Bounty> bountyTransactions;

  @Builder(builderMethodName = "bountyBuilder")
  public BountyGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                    @NonNull final BigDecimal stack, final List<Entries> entries,
                    @NonNull final BigDecimal bounty, final List<Bounty> bountyTransactions) {
    super(id, buyIn, stack, entries);
    this.bounty = bounty;
    this.bountyTransactions = bountyTransactions;
  }
}
