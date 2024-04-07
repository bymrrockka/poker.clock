package by.mrrockka.route.commands;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommand {

  BotApiMethodMessage process(final Update update);

  String commandPattern();

  default boolean isApplicable(final Update update) {
    return update.hasMessage() && update.getMessage().isCommand()
      && update.getMessage().getEntities().stream()
      .anyMatch(entity -> entity.getText().matches(commandPattern()));
  }

}
