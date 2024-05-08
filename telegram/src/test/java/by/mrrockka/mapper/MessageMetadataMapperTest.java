package by.mrrockka.mapper;

import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMetadataMapperTest {

  private final MessageMetadataMapper messageMetadataMapper = Mappers.getMapper(MessageMetadataMapper.class);

  @Test
  void givenMessage_whenAttemptToMap_shouldReturnMetadata() {
    assertThat(messageMetadataMapper.map(MessageCreator.message()))
      .isEqualTo(MessageMetadataCreator.domain());
  }

  @Test
  void givenMessageWithReplyTo_whenAttemptToMap_shouldReturnMetadata() {
    final var message = MessageCreator.message(msg -> msg.setReplyToMessage(MessageCreator.message()));

    final var replyTo = MessageMetadataCreator.domain();
    final var root = MessageMetadataCreator.domain(
      builder -> builder.replyTo(replyTo)
        .entities(List.of(MessageEntityCreator.domainEntity())));

    assertThat(messageMetadataMapper.map(message)).isEqualTo(root);
  }
}