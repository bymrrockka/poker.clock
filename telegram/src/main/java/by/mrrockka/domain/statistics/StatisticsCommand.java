package by.mrrockka.domain.statistics;

import by.mrrockka.domain.MessageMetadata;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record StatisticsCommand(@NonNull StatisticsType type, @NonNull MessageMetadata metadata) {
}
