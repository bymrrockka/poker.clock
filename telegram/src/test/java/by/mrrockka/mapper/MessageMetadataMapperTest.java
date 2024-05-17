package by.mrrockka.mapper;

import by.mrrockka.bot.properties.TelegramBotsProperties;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageMetadataMapperTest {
  private static final String BOTNAME = "bot";

  @Spy
  private MessageEntityMapper messageEntityMapper = new MessageEntityMapperImpl();
  @Mock
  private TelegramBotsProperties telegramBotsProperties;

  @InjectMocks
  private MessageMetadataMapperImpl messageMetadataMapper;

  @BeforeEach
  void beforeAll() {
    when(telegramBotsProperties.getNickname()).thenReturn(BOTNAME);
  }

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

  @Test
  void givenMessageWithBotMention_whenAttemptToMap_shouldReturnMetadataWithoutBotMention() {
    final var userMention = "@asdfasdf";
    final var messageText = "/%s @%s %s".formatted(BOTNAME, BOTNAME, userMention);

    final var message = MessageCreator
      .message(msg -> {
        msg.setText(messageText);
        msg.setEntities(
          List.of(
            MessageEntityCreator.apiCommand(messageText, "/%s".formatted(BOTNAME)),
            MessageEntityCreator.apiMention(messageText, "@%s".formatted(BOTNAME)),
            MessageEntityCreator.apiMention(messageText, userMention)
          ));
      });

    final var root = MessageMetadataCreator.domain(
      builder -> builder
        .command(messageText)
        .entities(List.of(
          MessageEntityCreator.domainEntity(entity -> entity
            .type(MessageEntityType.BOT_COMMAND)
            .text("/%s".formatted(BOTNAME))),
          MessageEntityCreator.domainMention(userMention)
        )));

    assertThat(messageMetadataMapper.map(message)).isEqualTo(root);
  }
}
