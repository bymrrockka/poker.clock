package by.mrrockka.domain.payout;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Debt(Player debtor, Entries debtorEntries, BigDecimal amount) {
}
