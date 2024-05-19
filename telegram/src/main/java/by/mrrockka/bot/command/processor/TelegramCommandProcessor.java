package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.mrrockka.domain.mesageentity.MessageEntityType.BOT_COMMAND;

public interface TelegramCommandProcessor {

  BotApiMethodMessage process(final Update update);

  default BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return null;
  }

  String commandPattern();

  default boolean isApplicable(final Update update) {
    return isMessageApplicable(update) || isEditedMessageApplicable(update);
  }

  default boolean isMessageApplicable(final Update update) {
    return update.hasMessage() && update.getMessage().getEntities().stream()
      .filter(entity -> BOT_COMMAND.value().equals(entity.getType()))
      .anyMatch(entity -> entity.getText().matches(commandPattern()));
  }

  default boolean isEditedMessageApplicable(final Update update) {
    return update.hasEditedMessage() && update.getEditedMessage().getEntities().stream()
      .filter(entity -> BOT_COMMAND.value().equals(entity.getType()))
      .anyMatch(entity -> entity.getText().matches(commandPattern()));
  }

}
