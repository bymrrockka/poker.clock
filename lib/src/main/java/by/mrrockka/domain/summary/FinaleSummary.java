package by.mrrockka.domain.summary;

import by.mrrockka.domain.player.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record FinaleSummary(@NonNull Integer position, @NonNull Person person, @NonNull BigDecimal amount) {
}
