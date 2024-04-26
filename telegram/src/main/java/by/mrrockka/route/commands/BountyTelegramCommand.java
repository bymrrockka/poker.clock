package by.mrrockka.route.commands;

import by.mrrockka.service.TelegramBountyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BountyTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/bounty$";

  private final TelegramBountyService telegramBountyService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramBountyService.storeBounty(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
