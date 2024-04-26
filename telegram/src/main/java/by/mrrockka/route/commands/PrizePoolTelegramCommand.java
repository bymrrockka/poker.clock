package by.mrrockka.route.commands;

import by.mrrockka.service.TelegramPrizePoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PrizePoolTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/prizepool$";

  private final TelegramPrizePoolService prizePoolService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return prizePoolService.storePrizePool(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
