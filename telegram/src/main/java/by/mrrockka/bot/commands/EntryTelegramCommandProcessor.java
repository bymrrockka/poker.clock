package by.mrrockka.bot.commands;

import by.mrrockka.service.EntryTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EntryTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/entry$";

  private final EntryTelegramService entryTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return entryTelegramService.storeEntry(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
