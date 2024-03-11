package by.mrrockka.route;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

//todo: rename to telegram command
interface TelegramCommand {

  //  todo: change return type to custom or List to support multiple commands in a message
  BotApiMethodMessage process(final Update update);

  default boolean isApplicable(final Update update) {
    return update.hasMessage() && update.getMessage().isCommand();
  }
}
