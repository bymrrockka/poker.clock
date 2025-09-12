package by.mrrockka.bot;

import by.mrrockka.bot.command.TelegramCommandProcessorFactory;
import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.creator.*;
import by.mrrockka.domain.PokerClockBotOptions;
import by.mrrockka.mapper.MessageMetadataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokerClockBotTest {

  private static final String COMMAND = "/command";
  private static final String MENTION = "@mention";
  private static final String COMMAND_TEXT = "%s text".formatted(COMMAND);
  private static final String MENTION_TEXT = "%s text".formatted(MENTION);

  @Mock
  private PokerClockAbsSender pokerClockAbsSender;
  @Mock
  private TelegramBotsProperties telegramBotsProperties;
  @Mock
  private TelegramCommandProcessorFactory telegramCommandProcessorFactory;
  @Mock
  private MessageMetadataMapper messageMetadataMapper;
  @Mock
  private TelegramCommandProcessor commandProcessor;
  @InjectMocks
  private PokerClockBot pokerClockBot;

  @Test
  void givenBotProperties_whenGetBotUsername_thenShouldReturnName() {
    final var expected = "name";
    when(telegramBotsProperties.getName()).thenReturn(expected);
    assertThat(pokerClockBot.getBotUsername()).isEqualTo(expected);
  }

  @Test
  void givenAbsSender_whenOnClosingExecuted_thenShouldShutdownSender() {
    pokerClockBot.onClosing();
    verify(pokerClockAbsSender).shutdown();
  }

  @Test
  void givenAbsSender_whenGetOptionsExecuted_thenShouldReturnOptions() {
    final var expected = new PokerClockBotOptions(List.of());
    when(pokerClockAbsSender.getOptions()).thenReturn(expected);
    assertThat(pokerClockBot.getOptions()).isEqualTo(expected);
  }

  @Test
  void givenBotProperties_whenGetTokenExecuted_thenShouldReturnToken() {
    final var expected = "token";
    when(telegramBotsProperties.getToken()).thenReturn(expected);
    assertThat(pokerClockBot.getBotToken()).isEqualTo(expected);
  }

  @Test
  void givenBotCommands_whenOnRegisterExecuted_thenShouldRegisterBotCommands() {
    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    pokerClockBot.onRegister();
  }

  @Test
  void givenUpdate_whenOnUpdateReceivedExecuted_thenShouldProcessMessage() throws TelegramApiException {
    final var message = MessageCreator.message(msg -> {
      msg.setText(COMMAND_TEXT);
      msg.setEntities(List.of(MessageEntityCreator.apiCommand(COMMAND_TEXT, COMMAND)));
    });
    final var update = UpdateCreator.Companion.update(message);
    final var metadata = MessageMetadataCreator.domain();
    final var sendMessage = SendMessageCreator.api();

    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    when(messageMetadataMapper.map(message)).thenReturn(metadata);
    when(telegramCommandProcessorFactory.provideProcessor(metadata)).thenReturn(commandProcessor);
    when(commandProcessor.process(metadata)).thenReturn(sendMessage);
    pokerClockBot.onUpdateReceived(update);

    verify(commandProcessor).process(metadata);
    verify(pokerClockAbsSender).execute(sendMessage);
  }

  @Test
  void givenUpdateWithPollAnswer_whenOnUpdateReceivedExecuted_thenShouldProcessUpdate() throws TelegramApiException {
//    todo: create a test
    final var message = MessageCreator.message(msg -> {
      msg.setText(COMMAND_TEXT);
      msg.setEntities(List.of(MessageEntityCreator.apiCommand(COMMAND_TEXT, COMMAND)));
    });
    final var update = UpdateCreator.Companion.update(message);
    final var metadata = MessageMetadataCreator.domain();
    final var sendMessage = SendMessageCreator.api();

    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    when(messageMetadataMapper.map(message)).thenReturn(metadata);
    when(telegramCommandProcessorFactory.provideProcessor(metadata)).thenReturn(commandProcessor);
    when(commandProcessor.process(metadata)).thenReturn(sendMessage);
    pokerClockBot.onUpdateReceived(update);

    verify(commandProcessor).process(metadata);
    verify(pokerClockAbsSender).execute(sendMessage);
  }

  private static Stream<Arguments> notProcessableMessages() {
    return Stream.of(
      Arguments.of(
        UpdateCreator.Companion.update(
          MessageCreator.message(msg -> {
            msg.setText(MENTION_TEXT);
            msg.setEntities(List.of(MessageEntityCreator.apiMention(MENTION_TEXT, MENTION)));
          }))
      ),
      Arguments.of(
        UpdateCreator.Companion.update((Message) null)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("notProcessableMessages")
  void givenNotProcessableUpdate_whenOnUpdateReceivedExecuted_thenShouldSkipProcessing(final Update update) {
    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    pokerClockBot.onUpdateReceived(update);

    verifyNoInteractions(commandProcessor, telegramCommandProcessorFactory, pokerClockAbsSender, messageMetadataMapper);
  }

  @Test
  void givenBotProperties_whenOnUpdateReceivedExecutedAndBotDisabled_thenShouldThrowException() {
    final var update = UpdateCreator.Companion.update(MessageCreator.message());

    when(telegramBotsProperties.getEnabled()).thenReturn(false);
    assertThatThrownBy(() -> pokerClockBot.onUpdateReceived(update))
      .isInstanceOf(BotIsNotEnabledException.class);
  }

}