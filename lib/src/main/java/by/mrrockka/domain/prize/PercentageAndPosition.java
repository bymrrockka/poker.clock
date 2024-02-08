package by.mrrockka.domain.prize;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PercentageAndPosition(BigDecimal percentage, int position) {
}
