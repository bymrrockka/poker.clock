package by.mrrockka.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record Payout(Player creditor, List<Debt> debts) {
}
