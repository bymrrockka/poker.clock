package by.mrrockka.domain;

import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;
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
  MessageMetadata replyTo,
  List<MessageEntity> entities,
  String fromNickname
) {
  public Optional<MessageMetadata> optReplyTo() {
    return Optional.ofNullable(replyTo);
  }

  public Optional<String> optFromNickname() {
    return Optional.ofNullable(fromNickname);
  }
}
