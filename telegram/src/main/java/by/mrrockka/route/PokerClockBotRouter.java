package by.mrrockka.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class PokerClockBotRouter extends TelegramLongPollingBot {
  private static final String BOT_NAME = "Poker calculator bot";
  @Value("${telegrambots.token}")
  private String token;
  @Autowired
  private List<CommandRoute> commandRoutes;

  @Override
  public void onUpdateReceived(Update update) {
    if (!commandRoutes.isEmpty()) {
      commandRoutes.stream()
        .filter(commandRoute -> commandRoute.isApplicable(update))
        .map(commandRoute -> commandRoute.process(update))
        .filter(Objects::nonNull)
        .forEach(this::executeMessage);
    } else {
      log.error("No routes found for ${}", update.getMessage());
    }

  }

  @Override
  public String getBotUsername() {
    return BOT_NAME;
  }

  @Override
  public String getBotToken() {
    return token;
  }

  private void executeMessage(BotApiMethodMessage message) {
    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Failed to execute ${} with error ${}", message, e.getMessage());
    }
  }
}
