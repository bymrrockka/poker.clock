package by.mrrockka.domain.payout;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record Payout(@NonNull PersonEntries personEntries, PersonWithdrawals personWithdrawals,
                     PersonBounties personBounties, List<Payer> payers) {
  public BigDecimal total() {
    return payers().stream().map(Payer::amount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }

  public Person person() {
    return personEntries().person();
  }
}
