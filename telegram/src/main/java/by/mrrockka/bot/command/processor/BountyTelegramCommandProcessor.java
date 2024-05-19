package by.mrrockka.bot.command.processor;

import by.mrrockka.service.BountyTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BountyTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/bounty$";

  private final BountyTelegramService bountyTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return bountyTelegramService.storeBounty(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
