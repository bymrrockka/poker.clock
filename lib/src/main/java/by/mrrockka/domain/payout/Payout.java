package by.mrrockka.domain.payout;

import by.mrrockka.domain.Player;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Payout(Player creditor, List<Debt> debts) {
  public BigDecimal totalDebts() {
    return debts().stream().map(Debt::amount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }
}
