package by.mrrockka.aspect;

import by.mrrockka.exception.BusinessException;
import by.mrrockka.route.PokerClockAbsSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class TelegramExceptionHandler {

  private final PokerClockAbsSender absSender;

  @SneakyThrows
  @AfterThrowing(
    pointcut = "execution(* *.onUpdatesReceived(..)) && args(updates)",
    argNames = "exception,updates",
    throwing = "exception")
  public void handleExceptions(final Throwable exception, final List<Update> updates) {
    String message = "Message: Exception occurred during processing a command";
    if (exception instanceof BusinessException) {
      message = exception.toString();
    }

    final var sendMessage = SendMessage.builder()
      .chatId(getCatId(updates))
      .text(message)
      .build();

    absSender.execute(sendMessage);
  }

  private Long getCatId(final List<Update> updates) {
    return updates.stream()
      .map(update -> update.getMessage().getChatId())
      .findFirst()
      .orElseThrow(ChatIdNotFoundException::new);
  }
}
