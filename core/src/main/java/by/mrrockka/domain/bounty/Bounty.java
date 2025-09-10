package by.mrrockka.domain.bounty;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
@Deprecated(forRemoval = true)
public record Bounty(@NonNull Person from, @NonNull Person to, @NonNull BigDecimal amount) {}