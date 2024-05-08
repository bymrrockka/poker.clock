package by.mrrockka.bot.commands;

import by.mrrockka.bot.keyboard.PokerClockBotKeyboard;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartTelegramCommand implements TelegramCommand {

  private static final String COMMAND = "^/start$";

  @Override
  public BotApiMethodMessage process(Update update) {
    return SendMessage.builder()
      .chatId(update.getMessage().getChatId())
      .text("Here I'm")
      .replyMarkup(new PokerClockBotKeyboard())
      .build();
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }
}
