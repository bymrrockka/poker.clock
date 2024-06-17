package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.statistics.StatisticsTelegramFacadeService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@RequiredArgsConstructor
public abstract class AbstractStatisticsTelegramCommandProcessor implements TelegramCommandProcessor {

  private final StatisticsTelegramFacadeService statisticsTelegramService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return statisticsTelegramService.retrieveStatistics(messageMetadata);
  }

}
