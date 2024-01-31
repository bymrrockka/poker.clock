package by.mrrockka.route;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ReentryCommandRoute implements CommandRoute {

  private static final String COMMAND = "/reentry";

  @Override
  public BotApiMethodMessage process(Update update) {
//    todo: store it at some point

    final var message = update.getMessage().getText();


    return null;
  }

  @Override
  public boolean isApplicable(Update update) {
    return CommandRoute.super.isApplicable(update)
      && update.getMessage().getText().contains(COMMAND);
  }
}
