package by.mrrockka.model.prize;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PrizeAndPosition(BigDecimal prize, int place) {
}
