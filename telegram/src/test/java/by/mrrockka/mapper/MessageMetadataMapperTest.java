package by.mrrockka.mapper;

import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMetadataMapperTest {

  private final MessageMetadataMapper messageMetadataMapper = Mappers.getMapper(MessageMetadataMapper.class);

  @Test
  void givenMessage_whenAttemptToMap_shouldReturnMetadata() {
    final var message = MessageCreator.message();
    final var messageMetadata = MessageMetadata.builder()
      .id(MessageCreator.MESSAGE_ID)
      .createdAt(MessageCreator.MESSAGE_TIMESTAMP.truncatedTo(ChronoUnit.SECONDS))
      .chatId(ChatCreator.CHAT_ID)
      .command(MessageCreator.MESSAGE_TEXT)
      .build();

    assertThat(messageMetadataMapper.map(message))
      .isEqualTo(messageMetadata);
  }

  @Test
  void givenMessageWithReplyTo_whenAttemptToMap_shouldReturnMetadata() {
    final var message = MessageCreator.message(msg -> {
      msg.setReplyToMessage(MessageCreator.message());
    });

    final var replyTo = MessageMetadataCreator.domain();
    final var root = MessageMetadataCreator.domain(builder -> builder.replyTo(replyTo));

    assertThat(messageMetadataMapper.map(message))
      .isEqualTo(root);
  }
}