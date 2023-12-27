package by.mrrockka.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Debt(Player debtor, BigDecimal amount) {
}
