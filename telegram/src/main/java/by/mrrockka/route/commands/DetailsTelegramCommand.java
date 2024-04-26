package by.mrrockka.route.commands;

import by.mrrockka.service.TelegramDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class DetailsTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/details";
  private final TelegramDetailsService telegramDetailsService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramDetailsService.calculatePayments(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
