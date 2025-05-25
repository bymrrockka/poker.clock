package by.mrrockka.domain;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record Bounty(@NonNull Person from, @NonNull Person to, @NonNull BigDecimal amount) {}