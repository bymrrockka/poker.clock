package by.mrrockka.mapper;

import by.mrrockka.bot.TelegramBotsProperties;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
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
  private static final String COMMAND = "/bot";
  private static final String BOT_NICKNAME = "bot";
  private static final String BOT_MENTION = "@%s".formatted(BOT_NICKNAME);

  @Spy
  private MessageEntityMapper messageEntityMapper = new MessageEntityMapperImpl();
  @Mock
  private TelegramBotsProperties telegramBotsProperties;

  @InjectMocks
  private MessageMetadataMapperImpl messageMetadataMapper;

  @BeforeEach
  void beforeAll() {
    when(telegramBotsProperties.getNickname()).thenReturn(BOT_NICKNAME);
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
    final var messageText = "%s %s %s".formatted(COMMAND, BOT_MENTION, userMention);

    final var message = MessageCreator
      .message(msg -> {
        msg.setText(messageText);
        msg.setEntities(
          List.of(
            MessageEntityCreator.apiCommand(messageText, COMMAND),
            MessageEntityCreator.apiMention(messageText, BOT_MENTION),
            MessageEntityCreator.apiMention(messageText, userMention)
          ));
      });

    final var root = MessageMetadataCreator.domain(
      builder -> builder
        .text(messageText)
        .entities(List.of(
          MessageEntityCreator.domainCommand(COMMAND),
          MessageEntityCreator.domainMention(userMention)
        )));

    assertThat(messageMetadataMapper.map(message)).isEqualTo(root);
  }

  @Test
  void givenCommandMessageWithBotNicknamePostfix_whenAttemptToMap_thenShouldReturnTextWithoutBotNickname() {
    final var actualText = COMMAND + BOT_MENTION;
    final var message = MessageCreator.message(actualText);

    final var expected = MessageMetadataCreator
      .domain(metadata -> metadata
        .text(COMMAND)
        .entities(List.of(MessageEntityCreator.domainCommand(COMMAND))));

    assertThat(messageMetadataMapper.map(message)).isEqualTo(expected);
  }
}
