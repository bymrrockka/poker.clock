package by.mrrockka.route;

import by.mrrockka.domain.PokerClockBotOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import java.util.List;

import static org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates.*;

@Component
public class PokerClockAbsSender extends DefaultAbsSender {

  private static final PokerClockBotOptions BOT_OPTIONS = PokerClockBotOptions.builder()
    .allowedUpdates(List.of(
      EDITEDMESSAGE,
      MESSAGE,
      CHANNELPOST,
      EDITEDCHANNELPOST)
    ).build();

  protected PokerClockAbsSender(@Value("${telegrambots.token}") final String botToken) {
    super(BOT_OPTIONS, botToken);
  }

  public void shutdown() {
    exe.shutdown();
  }
}
