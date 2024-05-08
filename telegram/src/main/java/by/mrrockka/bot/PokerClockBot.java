package by.mrrockka.bot;

import by.mrrockka.bot.commands.TelegramCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  private final PokerClockAbsSender pokerClockAbsSender;
  private final List<TelegramCommand> telegramCommands;

  @Value("${telegrambots.name}")
  private String botName;

  @Override
  public void onUpdateReceived(final Update update) {
//    todo: add logic to process edited message
    if (update.hasMessage() && update.getMessage().isCommand()) {
      telegramCommands.stream()
        .filter(telegramCommand -> telegramCommand.isApplicable(update))
        .map(telegramCommand -> telegramCommand.process(update))
        .filter(Objects::nonNull)
        .findFirst()
        .map(this::executeMessage)
        .orElseThrow(NoRoutesFoundException::new);
    }
  }

  @Override
  public String getBotUsername() {
    return botName;
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
