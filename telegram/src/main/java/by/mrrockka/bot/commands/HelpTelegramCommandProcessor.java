package by.mrrockka.bot.commands;

import by.mrrockka.service.help.HelpTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class HelpTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/help$";
  private final HelpTelegramService helpTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return helpTelegramService.sendHelpInformation(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
