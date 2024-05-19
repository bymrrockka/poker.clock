package by.mrrockka.service.help;

import lombok.Builder;

@Builder
public record CommandDescription(boolean enabled, String description, String details) {
}
