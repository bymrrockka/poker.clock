package by.mrrockka.aspect;

import by.mrrockka.exception.BusinessException;
import by.mrrockka.route.PokerClockBotRouter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Aspect
@Component
@RequiredArgsConstructor
public class TelegramExceptionHandler {

  private final PokerClockBotRouter pokerClockBotRouter;

  @Pointcut("execution(* *..PokerClockBotRouter.onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update))")
  public void onUpdateReceived() {}

  @AfterThrowing(pointcut = "onUpdateReceived() && args(update,..)", throwing = "exception")
  public void handleExceptions(final Exception exception, final Update update) throws Throwable {

    String message = exception.getMessage();
    if (exception instanceof BusinessException) {
      message = exception.toString();
    }

    final var sendMessage = SendMessage.builder()
      .chatId(update.getMessage().getChatId())
      .text(message)
      .build();

    pokerClockBotRouter.execute(sendMessage);
  }

}
