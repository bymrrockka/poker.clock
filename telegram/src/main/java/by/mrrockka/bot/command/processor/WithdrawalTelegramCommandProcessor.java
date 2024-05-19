package by.mrrockka.bot.command.processor;

import by.mrrockka.service.WithdrawalTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class WithdrawalTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/withdrawal$";

  private final WithdrawalTelegramService withdrawalTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return withdrawalTelegramService.storeWithdrawal(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }
}
