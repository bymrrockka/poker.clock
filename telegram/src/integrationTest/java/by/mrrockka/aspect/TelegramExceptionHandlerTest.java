package by.mrrockka.aspect;

import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.exception.BusinessException;
import by.mrrockka.route.PokerClockAbsSender;
import by.mrrockka.route.PokerClockBot;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TelegramExceptionHandlerTest {

  @SpyBean
  private PokerClockBot pokerClockBot;
  @MockBean
  private PokerClockAbsSender pokerClockAbsSender;
  @MockBean
  private DataSource dataSource;

  private static Stream<Arguments> exceptions() {
    return Stream.of(
      Arguments.of(
        "Message: Exception occurred during processing a command",
        new RuntimeException("Any message")
      ),
      Arguments.of(
        """
          Message: No stack specified
          Readable code: validation.error
          """,
        new BusinessException("No stack specified", "validation.error")
      )
    );
  }

  @ParameterizedTest
  @MethodSource("exceptions")
  void whenRouterThrowsException_shouldSendMessageWithExceptionMessage(final String text,
                                                                       final Throwable ex) throws TelegramApiException {
    final var updates = List.of(UpdateCreator.update(MessageCreator.message("")));
    final var expected = SendMessage.builder()
      .chatId(ChatCreator.CHAT_ID)
      .text(text)
      .build();

    doThrow(ex).when(pokerClockBot).onUpdateReceived(updates.get(0));
    assertThatThrownBy(() -> pokerClockBot.onUpdatesReceived(updates));

    verify(pokerClockAbsSender).execute(expected);
  }

}