package by.mrrockka.domain.prize;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PositionPrize(int position, BigDecimal percentage) {
}
