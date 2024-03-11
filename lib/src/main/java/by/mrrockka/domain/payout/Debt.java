package by.mrrockka.domain.payout;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record Debt(Entries entries, Withdrawals withdrawals, BigDecimal amount) {
}
