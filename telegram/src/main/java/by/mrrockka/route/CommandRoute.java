package by.mrrockka.route;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

//todo: rename to telegram command
interface CommandRoute {

  BotApiMethodMessage process(final Update update);

  default boolean isApplicable(final Update update) {
    return update.hasMessage() && update.getMessage().isCommand();
  }
}
