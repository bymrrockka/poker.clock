package by.mrrockka.bot.commands;

import by.mrrockka.service.TelegramEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EntryTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/entry$";

  private final TelegramEntryService telegramEntryService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramEntryService.storeEntry(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
