package by.mrrockka.bot;

import by.mrrockka.bot.command.TelegramCommandProcessorFactory;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.UpdateBotCommandsService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.util.WebhookUtils;

@Slf4j
//@Component
@RequiredArgsConstructor
public class PokerClockBot implements LongPollingBot {

  private final PokerClockAbsSender absSender;
  private final UpdateBotCommandsService updateBotCommandsService;
  private final TelegramBotsProperties telegramBotsProperties;
  private final TelegramCommandProcessorFactory telegramCommandProcessorFactory;
  private final MessageMetadataMapper messageMetadataMapper;

  @Override
  public void onUpdateReceived(final Update update) {
//    todo: add logic to process edited message
    if (!telegramBotsProperties.getEnabled()) {
      throw new BotIsNotEnabledException();
    }
    if (isProcessable(update)) {
      final var messageMetadata = messageMetadataMapper.map(update.getMessage());

      log.debug("Processing %s command with message id %s from chat id %s."
                  .formatted(messageMetadata.getCommand().getText(), messageMetadata.getId(),
                             messageMetadata.getChatId()));

      final var commandProcessor = telegramCommandProcessorFactory.provideProcessor(messageMetadata);
      executeMessage(commandProcessor.process(messageMetadata));
    }
  }

  @Override
  public String getBotUsername() {
    return telegramBotsProperties.getName();
  }

  @SneakyThrows
  private void executeMessage(final BotApiMethodMessage message) {
    absSender.execute(message);
  }

  @Override
  public void clearWebhook() throws TelegramApiRequestException {
    if (telegramBotsProperties.getEnabled()) {
      WebhookUtils.clearWebhook(absSender);
    }
  }

  @Override
  public void onClosing() {
    absSender.shutdown();
  }

  @Override
  public BotOptions getOptions() {
    return absSender.getOptions();
  }

  @Override
  public String getBotToken() {
    return telegramBotsProperties.getToken();
  }

  private boolean isProcessable(final Update update) {
    return update.hasMessage() && update.getMessage().isCommand();
  }

  @Override
  public void onRegister() {
    if (telegramBotsProperties.getEnabled()) {
      updateBotCommandsService.updateBotCommands();
    }
  }
}
