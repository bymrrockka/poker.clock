package by.mrrockka.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
@Slf4j
public class PokerClockAbsSender extends DefaultAbsSender {

  public PokerClockAbsSender(@Autowired final DefaultBotOptions botOptions,
                             @Autowired final TelegramBotsProperties telegramBotsProperties) {
    super(botOptions, telegramBotsProperties.getToken());
  }

  public void shutdown() {
    exe.shutdown();
  }
}
