package by.mrrockka.domain.mesageentity;

import lombok.Builder;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

@Builder
public record MessageEntity(@NonNull MessageEntityType type, @NonNull String text, User user) {
}
