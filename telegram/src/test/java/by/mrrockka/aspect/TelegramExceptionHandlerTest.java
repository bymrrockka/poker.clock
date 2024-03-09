package by.mrrockka.aspect;

import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.route.PokerClockBotRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TelegramExceptionHandlerTest {

  @SpyBean
  private PokerClockBotRouter pokerClockBotRouter;

  @MockBean
  private DataSource dataSource;

  @Autowired
  private TelegramExceptionHandler telegramExceptionHandler;

  @Test
  void whenRouterThrowsException_shouldSendMessageWithExceptionMessage() throws TelegramApiException {
    final var update = UpdateCreator.update(MessageCreator.message(""));
    final var text = "Message";
    final var ex = new RuntimeException(text);
    final var expected = SendMessage.builder()
      .chatId(ChatCreator.CHAT_ID)
      .text(text)
      .build();

    doThrow(ex).when(pokerClockBotRouter).onUpdateReceived(update);
    assertThatThrownBy(() -> pokerClockBotRouter.onUpdateReceived(update));

    verify(pokerClockBotRouter).execute(expected);
  }

}