package by.mrrockka.bot.commands;

import by.mrrockka.service.TelegramHelpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class HelpTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/help$";
  private final TelegramHelpService telegramHelpService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramHelpService.calculatePayments(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
