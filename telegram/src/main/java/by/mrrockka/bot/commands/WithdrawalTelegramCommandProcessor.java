package by.mrrockka.bot.commands;

import by.mrrockka.service.TelegramWithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class WithdrawalTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/withdrawal$";

  private final TelegramWithdrawalService telegramWithdrawalService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramWithdrawalService.storeWithdrawal(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }
}
