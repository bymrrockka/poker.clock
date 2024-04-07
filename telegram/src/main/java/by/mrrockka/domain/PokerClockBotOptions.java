package by.mrrockka.domain;

import lombok.Builder;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;

public class PokerClockBotOptions extends DefaultBotOptions {

  @Builder
  public PokerClockBotOptions(final List<String> allowedUpdates) {
    this.setAllowedUpdates(allowedUpdates);
  }
}
