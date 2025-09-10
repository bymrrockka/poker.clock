package by.mrrockka.domain.prize;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Deprecated(forRemoval = true)
public record PositionPrize(int position, BigDecimal percentage) {
}
