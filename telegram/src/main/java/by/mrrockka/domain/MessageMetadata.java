package by.mrrockka.domain;

import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Builder
public record MessageMetadata(
  @NonNull
  Long chatId,
  @NonNull
  Instant createdAt,
  @NonNull
  Integer id,
  String text,
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

  public Stream<MessageEntity> mentions() {
    return entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.MENTION));
  }

  public MessageEntity command() {
    return entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.BOT_COMMAND))
      .findFirst()
      .orElseThrow(() -> new ProcessingRestrictedException("Message has no command."));
  }

}
