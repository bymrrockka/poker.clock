package by.mrrockka.domain.payout;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Deprecated(forRemoval = true)
public record Payer(@NonNull PersonEntries personEntries, PersonWithdrawals personWithdrawals,
                    PersonBounties personBounties, BigDecimal amount) {

  public Person person() {
    return personEntries().person();
  }
}
