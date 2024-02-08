package by.mrrockka.domain;

import by.mrrockka.domain.player.Person;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Bounty(Person from, Person to, BigDecimal amount) {
}
