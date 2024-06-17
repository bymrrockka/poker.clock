package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.FinalePlacesTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class FinalePlacesTelegramCommandProcessor implements TelegramCommandProcessor {
  private final FinalePlacesTelegramService finalePlacesTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return finalePlacesTelegramService.storePrizePool(messageMetadata);
  }

}
