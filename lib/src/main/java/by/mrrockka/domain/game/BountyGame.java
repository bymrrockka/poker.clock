package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
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
public final class BountyGame extends Game {

  FinaleSummary finaleSummary;
  @NonNull
  BigDecimal bountyAmount;
  List<Bounty> bountyList;

  @Builder(builderMethodName = "bountyBuilder")
  public BountyGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                    @NonNull final BigDecimal stack, final List<PersonEntries> entries,
                    @NonNull final BigDecimal bountyAmount, final List<Bounty> bountyList,
                    final FinaleSummary finaleSummary) {
    super(id, buyIn, stack, entries);
    this.bountyAmount = bountyAmount;
    this.bountyList = bountyList;
    this.finaleSummary = finaleSummary;
  }
}
