package by.mrrockka.route;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CalculatePaymentsCommandRoute implements CommandRoute {
  private static final String CALCULATE_COMMAND = "^/calculate$";

  @Override
  public BotApiMethodMessage process(final Update update) {
    return SendMessage.builder()
      .chatId(update.getMessage().getChatId())
      .text(update.getMessage().getText())
      .build();
  }

  @Override
  public boolean isApplicable(final Update update) {
    return CommandRoute.super.isApplicable(update) &&
      update.getMessage().getText().matches(CALCULATE_COMMAND);
  }

}
