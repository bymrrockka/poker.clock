package by.mrrockka.bot.command.processor;

import by.mrrockka.service.FinalePlacesTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class FinalePlacesTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/finaleplaces$";

  private final FinalePlacesTelegramService finalePlacesTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return finalePlacesTelegramService.storePrizePool(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
