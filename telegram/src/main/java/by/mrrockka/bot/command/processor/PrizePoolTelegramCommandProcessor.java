package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.PrizePoolTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class PrizePoolTelegramCommandProcessor implements TelegramCommandProcessor {
  private final PrizePoolTelegramService prizePoolService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return prizePoolService.storePrizePool(messageMetadata);
  }

}
