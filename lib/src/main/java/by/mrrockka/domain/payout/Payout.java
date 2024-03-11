package by.mrrockka.domain.payout;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Payout(Player creditor, Entries creditorEntries, List<Debt> debts) {
  public BigDecimal totalDebts() {
    return debts().stream().map(Debt::amount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }
}
