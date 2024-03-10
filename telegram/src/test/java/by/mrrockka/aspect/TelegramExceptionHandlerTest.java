package by.mrrockka.aspect;

import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.mapper.game.NoStackException;
import by.mrrockka.route.PokerClockBotRouter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class TelegramExceptionHandlerTest {

  @MockBean
  private PokerClockBotRouter pokerClockBotRouter;

  @MockBean
  private DataSource dataSource;

  @Autowired
  private TelegramExceptionHandler telegramExceptionHandler;

  private static Stream<Arguments> exeptions() {
    return Stream.of(
      Arguments.of(
        "Message",
        new RuntimeException("Message")
      ),
      Arguments.of(
        """
          ERROR
          Message: No stack specified.
          Readable code: validation.error
          """,
        new NoStackException()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("exeptions")
  void whenRouterThrowsException_shouldSendMessageWithExceptionMessage(final String text,
                                                                       final Throwable ex) throws TelegramApiException {
    final var updates = List.of(UpdateCreator.update(MessageCreator.message("")));
    final var expected = SendMessage.builder()
      .chatId(ChatCreator.CHAT_ID)
      .text(text)
      .build();

    doCallRealMethod().when(pokerClockBotRouter).onUpdatesReceived(updates);
    doThrow(ex).when(pokerClockBotRouter).onUpdateReceived(updates.get(0));
    assertThatThrownBy(() -> pokerClockBotRouter.onUpdatesReceived(updates));

    when(pokerClockBotRouter.execute(expected)).thenReturn(MessageCreator.message());
  }

}