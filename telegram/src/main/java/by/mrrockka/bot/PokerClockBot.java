package by.mrrockka.bot;

import by.mrrockka.bot.command.TelegramCommandProcessorFactory;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.UpdateBotCommandsService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.util.WebhookUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokerClockBot implements LongPollingBot {

  private final PokerClockAbsSender pokerClockAbsSender;
  private final UpdateBotCommandsService updateBotCommandsService;
  private final TelegramBotsProperties telegramBotsProperties;
  private final TelegramCommandProcessorFactory telegramCommandProcessorFactory;
  private final MessageMetadataMapper messageMetadataMapper;

  @Override
  public void onUpdateReceived(final Update update) {
    if (!telegramBotsProperties.isEnabled()) {
      throw new BotIsNotEnabledException();
    }

//    todo: add logic to process edited message
    if (isProcessable(update)) {
      final var messageMetadata = messageMetadataMapper.map(update.getMessage());

      log.debug("Processing {\n%s\n} message from %s chat id."
                  .formatted(messageMetadata.text(), messageMetadata.chatId()));

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
    pokerClockAbsSender.execute(message);
  }

  @Override
  public void clearWebhook() throws TelegramApiRequestException {
    WebhookUtils.clearWebhook(pokerClockAbsSender);
  }

  @Override
  public void onClosing() {
    pokerClockAbsSender.shutdown();
  }

  @Override
  public BotOptions getOptions() {
    return pokerClockAbsSender.getOptions();
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
    updateBotCommandsService.updateBotCommands();
  }
}
