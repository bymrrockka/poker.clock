package by.mrrockka.service;

import by.mrrockka.bot.properties.BotProperties;
import by.mrrockka.mapper.HelpMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramHelpService {

  private static final String HELP_COMMAND = "help";

  private final BotProperties botProperties;
  private final MessageMetadataMapper messageMetadataMapper;
  private final HelpMessageMapper helpMessageMapper;

  public BotApiMethodMessage sendHelpInformation(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var description = helpMessageMapper.map(messageMetadata)
      .map(command -> botProperties.getCommands().get(command))
      .orElse(botProperties.getCommands().get(HELP_COMMAND));

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(description.details())
      .build();
  }

}
