package by.mrrockka.service.help;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.parser.HelpMessageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpTelegramService {
  private static final String HELP_COMMAND = "help";

  private final BotDescriptionProperties botDescriptionProperties;
  private final HelpMessageParser helpMessageParser;

  public BotApiMethodMessage sendHelpInformation(final MessageMetadata messageMetadata) {
    final var description = helpMessageParser.parse(messageMetadata)
      .map(command -> botDescriptionProperties.getCommands().get(command))
      .orElse(botDescriptionProperties.getCommands().get(HELP_COMMAND));

    return SendMessage.builder()
      .chatId(messageMetadata.getChatId())
      .text(description.details())
      .build();
  }

}
