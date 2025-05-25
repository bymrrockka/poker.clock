package by.mrrockka.domain.game;

import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.summary.finale.FinaleSummary;
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
public final class BountyGame extends TournamentGame {

  @NonNull
  BigDecimal bountyAmount;
  List<Bounty> bountyList;

  @Builder(builderMethodName = "bountyBuilder")
  public BountyGame(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                    @NonNull final BigDecimal stack, final List<PersonEntries> entries,
                    @NonNull final BigDecimal bountyAmount, final List<Bounty> bountyList,
                    final Instant finishedAt, final FinaleSummary finaleSummary) {
    super(id, buyIn, stack, entries, finishedAt, finaleSummary);
    this.bountyAmount = bountyAmount;
    this.bountyList = bountyList;
  }
}
