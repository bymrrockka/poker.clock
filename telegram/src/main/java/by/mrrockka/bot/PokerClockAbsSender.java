package by.mrrockka.bot;

import by.mrrockka.bot.properties.TelegramBotsProperties;
import by.mrrockka.domain.PokerClockBotOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import java.util.List;

import static org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates.EDITEDMESSAGE;
import static org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates.MESSAGE;

@Component
public class PokerClockAbsSender extends DefaultAbsSender {

  private static final PokerClockBotOptions BOT_OPTIONS = PokerClockBotOptions.builder()
    .allowedUpdates(List.of(
      EDITEDMESSAGE,
      MESSAGE)
    ).build();

  protected PokerClockAbsSender(@Autowired final TelegramBotsProperties telegramBotsProperties) {
    super(BOT_OPTIONS, telegramBotsProperties.getToken());
    ;
  }

  public void shutdown() {
    exe.shutdown();
  }
}
