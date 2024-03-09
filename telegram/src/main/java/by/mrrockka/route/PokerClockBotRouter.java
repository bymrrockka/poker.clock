package by.mrrockka.route;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

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
    }
    if (nonNull(update.getMessage())) {
      log.error("No routes found for \"${}\"", update.getMessage());
      throw new NoRoutesFoundException(update.getMessage().getText());
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

  @SneakyThrows
  private void executeMessage(final BotApiMethodMessage message) {
    execute(message);
  }

}
