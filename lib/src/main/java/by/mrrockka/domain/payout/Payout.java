package by.mrrockka.domain.payout;

import by.mrrockka.domain.Player;
import lombok.Builder;

import java.util.List;

@Builder
public record Payout(Player creditor, List<Debt> debts) {
}
