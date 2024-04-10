package by.mrrockka.creator;

import by.mrrockka.domain.MessageMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageMetadataCreator {

  public static MessageMetadata domain() {
    return domain(null);
  }

  public static MessageMetadata domain(
    final Consumer<MessageMetadata.MessageMetadataBuilder> messageMetadataBuilderConsumer) {

    final var messageMetadataBuilder = MessageMetadata.builder()
      .id(MessageCreator.MESSAGE_ID)
      .createdAt(MessageCreator.MESSAGE_TIMESTAMP.truncatedTo(ChronoUnit.SECONDS))
      .chatId(ChatCreator.CHAT_ID)
      .command(MessageCreator.MESSAGE_TEXT)
      .entities(List.of(MessageEntityCreator.domainEntity()));

    if (nonNull(messageMetadataBuilderConsumer)) {
      messageMetadataBuilderConsumer.accept(messageMetadataBuilder);
    }

    return messageMetadataBuilder.build();
  }

}
