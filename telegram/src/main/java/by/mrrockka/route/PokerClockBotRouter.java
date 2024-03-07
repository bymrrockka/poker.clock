package by.mrrockka.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class PokerClockBotRouter extends TelegramLongPollingBot {
  private static final String BOT_NAME = "Poker calculator bot";
  @Value("${telegrambots.token}")
  private String token;
  private final List<CommandRoute> commandRoutes;

  @Override
  public void onUpdateReceived(final Update update) {
    if (!commandRoutes.isEmpty()) {
      commandRoutes.stream()
        .filter(commandRoute -> commandRoute.isApplicable(update))
        .map(commandRoute -> commandRoute.process(update))
        .filter(Objects::nonNull)
        .forEach(this::executeMessage);
    } else {
//      todo: add error manager
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

  private void executeMessage(final BotApiMethodMessage message) {
    try {
      execute(message);
    } catch (final TelegramApiException telegramApiException) {
//      todo: add error manager
      log.error("Failed to execute ${} with error ${}", message, telegramApiException.getMessage());
    }
  }
}
