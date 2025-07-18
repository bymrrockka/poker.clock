package by.mrrockka.aspect;

import by.mrrockka.bot.PokerClockAbsSender;
import by.mrrockka.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Profile("!no-exception-handler")
public class TelegramExceptionHandler {

  private final PokerClockAbsSender absSender;

  @SneakyThrows
  @AfterThrowing(
    pointcut = "execution(* *.onUpdatesReceived(..)) && args(updates)",
    argNames = "exception,updates",
    throwing = "exception")
  public void handleExceptions(final Throwable exception, final List<Update> updates) {
    String message = "Exception occurred during processing a command";
    if (exception instanceof BusinessException) {
      message = ((BusinessException) exception).getMessage();
    }

    final var sendMessage = SendMessage.builder()
      .chatId(getChatId(updates))
      .text(message)
      .build();

    absSender.execute(sendMessage);
  }

  private Long getChatId(final List<Update> updates) {
    return updates.stream()
      .map(update -> update.getMessage().getChatId())
      .findFirst()
      .orElseThrow(ChatIdNotFoundException::new);
  }
}
