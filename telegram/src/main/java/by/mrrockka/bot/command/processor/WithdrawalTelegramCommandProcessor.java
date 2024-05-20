package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.WithdrawalTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class WithdrawalTelegramCommandProcessor implements TelegramCommandProcessor {
  private final WithdrawalTelegramService withdrawalTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return withdrawalTelegramService.storeWithdrawal(messageMetadata);
  }

}
