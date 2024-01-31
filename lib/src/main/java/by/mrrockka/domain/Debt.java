package by.mrrockka.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Debt(Player debtor, BigDecimal amount) {
}
