package by.mrrockka.domain;

import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;
import java.util.Optional;

@Builder
public record MessageMetadata(
  @NonNull
  Long chatId,
  @NonNull
  Instant createdAt,
  @NonNull
  Integer id,
  String command,
  MessageMetadata replyTo
) {
  public Optional<MessageMetadata> optReplyTo() {
    return Optional.ofNullable(replyTo);
  }
}
