package by.mrrockka.route;

import by.mrrockka.service.TelegramEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EntryCommandRoute implements CommandRoute {
  private static final List<String> COMMANDS = List.of("/entry", "/reentry");

  private final TelegramEntryService telegramEntryService;

  @Override
  public BotApiMethodMessage process(Update update) {
    return telegramEntryService.storeEntry(update);
  }

  @Override
  public boolean isApplicable(Update update) {
    return CommandRoute.super.isApplicable(update)
      && COMMANDS.stream().anyMatch(command -> update.getMessage().getText().contains(command));
  }
}
