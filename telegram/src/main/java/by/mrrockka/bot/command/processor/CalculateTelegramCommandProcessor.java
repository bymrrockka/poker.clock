package by.mrrockka.bot.command.processor;

import by.mrrockka.service.CalculationTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CalculateTelegramCommandProcessor implements TelegramCommandProcessor {

  private static final String COMMAND = "^/calculate$";

  private final CalculationTelegramService calculationTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return calculationTelegramService.calculatePayouts(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
