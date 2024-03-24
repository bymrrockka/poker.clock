package by.mrrockka.route;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokerClockBotRouter extends TelegramLongPollingBot {
  private static final String BOT_NAME = "Poker calculator bot";
  @Value("${telegrambots.token}")
  private String token;
  private final List<TelegramCommand> telegramCommands;

  @Override
  public void onUpdateReceived(final Update update) {
    telegramCommands.stream()
      .filter(telegramCommand -> telegramCommand.isApplicable(update))
      .map(telegramCommand -> telegramCommand.process(update))
      .filter(Objects::nonNull)
      .findFirst()
      .map(this::executeMessage)
      .orElseThrow(() -> new NoRoutesFoundException(update.getMessage().getText()));
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
  private Message executeMessage(final BotApiMethodMessage message) {
    return execute(message);
  }

}
