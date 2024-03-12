package by.mrrockka.domain.summary.finale;

import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record FinalePlaceSummary(@NonNull Integer position, @NonNull Person person, @NonNull BigDecimal amount) {
}
