package by.mrrockka.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PrizeAndPosition(BigDecimal prize, int place) {
}
