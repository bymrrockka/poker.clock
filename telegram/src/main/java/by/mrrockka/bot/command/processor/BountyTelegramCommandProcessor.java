package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.BountyTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class BountyTelegramCommandProcessor implements TelegramCommandProcessor {
  private final BountyTelegramService bountyTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return bountyTelegramService.storeBounty(messageMetadata);
  }

}
