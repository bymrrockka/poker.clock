package by.mrrockka.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Bounty(Person from, Person to, BigDecimal amount) {
}
