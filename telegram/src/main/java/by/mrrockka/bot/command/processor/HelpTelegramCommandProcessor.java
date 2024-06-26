package by.mrrockka.bot.command.processor;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.help.HelpTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class HelpTelegramCommandProcessor implements TelegramCommandProcessor {
  private final HelpTelegramService helpTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return helpTelegramService.sendHelpInformation(messageMetadata);
  }

}
