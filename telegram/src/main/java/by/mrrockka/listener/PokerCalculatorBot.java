package by.mrrockka.listener;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class PokerCalculatorBot extends TelegramLongPollingBot {
  private static final String BOT_NAME = "Poker calculator bot";
  @Value("${telegrambots.token}")
  private String token;

  private List<String> messages = new ArrayList<>();

  @Override
  public void onUpdateReceived(Update update) {
    messages.add(update.getMessage().toString());
  }

  @Override
  public String getBotUsername() {
    return BOT_NAME;
  }

  @Override
  public String getBotToken() {
    return token;
  }
}
