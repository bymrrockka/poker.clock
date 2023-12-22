package by.mrrockka.model;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;

@Builder
public record Debt(Player debtor, @With BigDecimal amount) {
}
