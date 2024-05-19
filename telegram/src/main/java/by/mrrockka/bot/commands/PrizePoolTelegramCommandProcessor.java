package by.mrrockka.bot.commands;

import by.mrrockka.service.PrizePoolTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PrizePoolTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/prizepool$";

  private final PrizePoolTelegramService prizePoolService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return prizePoolService.storePrizePool(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
