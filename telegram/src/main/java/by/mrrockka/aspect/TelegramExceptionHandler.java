package by.mrrockka.aspect;

import by.mrrockka.exception.BusinessException;
import lombok.SneakyThrows;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Aspect
@Component
public class TelegramExceptionHandler {

  @SneakyThrows
  @AfterThrowing(pointcut = "execution(* *.onUpdatesReceived(..)) && target(router) && args(updates)", throwing = "exception")
  public void handleExceptions(final Throwable exception, final AbsSender router, final List<Update> updates) {
    String message = exception.getMessage();
    if (exception instanceof BusinessException) {
      message = exception.toString();
    }

    final var sendMessage = SendMessage.builder()
      .chatId(getCatId(updates))
      .text(message)
      .build();

    router.execute(sendMessage);
  }

  private Long getCatId(final List<Update> updates) {
    return updates.stream()
      .map(update -> update.getMessage().getChatId())
      .findFirst()
      .orElseThrow(ChatIdNotFoundException::new);
  }
}
