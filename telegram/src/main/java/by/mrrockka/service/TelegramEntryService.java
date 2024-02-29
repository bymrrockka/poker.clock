package by.mrrockka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class TelegramEntryService {

  private final EntriesService entriesService;

  public BotApiMethodMessage storeEntry(Update update) {
    final var command = update.getMessage().getText();

/*todo:
   - extract chat id from command
   - find game id by chat id and pinned message timestamp or last created game in chat
   - find game by game id and get buy-in amount
   - find person id by telegram from message text
   -- store persons if command is /entry and there is no persons in db with this telegram
   - store entry for person id, game id, amount and default create at to current message date
   */
    return null;
  }
}
