package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

public interface TelegramCommandProcessor {

  BotApiMethodMessage process(final MessageMetadata messageMetadata);

}
