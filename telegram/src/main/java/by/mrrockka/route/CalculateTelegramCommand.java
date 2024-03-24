package by.mrrockka.route;

import by.mrrockka.service.TelegramCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CalculateTelegramCommand implements TelegramCommand {

  @Value("telegrambots.name")
  private String botName;

  private static final String COMMAND = "/calculate([\\s]*|%s)";

  private final TelegramCalculationService telegramCalculationService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramCalculationService.calculatePayments(update);
  }

  @Override
  public boolean isApplicable(final Update update) {
    return TelegramCommand.super.isApplicable(update) &&
      update.getMessage().getText().matches(COMMAND.formatted(botName));
  }

}
