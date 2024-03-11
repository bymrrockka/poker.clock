package by.mrrockka.domain.payout;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record Payout(Entries entries, Withdrawals withdrawals, List<Debt> debts) {
  public BigDecimal totalDebts() {
    return debts().stream().map(Debt::amount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }

  public Person person() {
    return entries().person();
  }
}
