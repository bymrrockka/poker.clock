package by.mrrockka.route;

import by.mrrockka.route.commands.TelegramCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.util.WebhookUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokerClockBot implements LongPollingBot {

  private static final String BOT_NAME = "Poker calculator bot";
  private final PokerClockAbsSender pokerClockAbsSender;
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

  @SneakyThrows
  private Message executeMessage(final BotApiMethodMessage message) {
    return pokerClockAbsSender.execute(message);
  }

  @Override
  public void clearWebhook() throws TelegramApiRequestException {
    WebhookUtils.clearWebhook(pokerClockAbsSender);
  }

  @Override
  public void onClosing() {
    pokerClockAbsSender.shutdown();
  }

  @Override
  public BotOptions getOptions() {
    return pokerClockAbsSender.getOptions();
  }

  @Override
  public String getBotToken() {
    return pokerClockAbsSender.getBotToken();
  }
}
