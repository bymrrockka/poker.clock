package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class PollTelegramCommandProcessor implements TelegramCommandProcessor {


  @Override
  public BotApiMethodMessage process(MessageMetadata messageMetadata) {
    return null;
  }
}
