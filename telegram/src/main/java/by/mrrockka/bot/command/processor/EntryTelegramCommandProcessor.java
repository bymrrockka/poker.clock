package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.EntryTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class EntryTelegramCommandProcessor implements TelegramCommandProcessor {
  private final EntryTelegramService entryTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return entryTelegramService.storeEntry(messageMetadata);
  }

}
