package by.mrrockka.domain.statistics;

import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Optional;

@Builder
public record PlayerInGameStatistics(
  @NonNull PersonEntries personEntries,
  PersonWithdrawals personWithdrawals,
  PersonBounties personBounties,
  @NonNull BigDecimal moneyInGame) {

  public Optional<PersonWithdrawals> optPersonWithdrawals() {
    return Optional.ofNullable(personWithdrawals);
  }

  public Optional<PersonBounties> optPersonBounties() {
    return Optional.ofNullable(personBounties);
  }
}
