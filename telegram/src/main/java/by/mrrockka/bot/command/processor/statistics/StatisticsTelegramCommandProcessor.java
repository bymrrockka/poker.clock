package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.service.statistics.StatisticsTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StatisticsTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/(game|my)_stats$";
  private final StatisticsTelegramService statisticsTelegramService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return statisticsTelegramService.retrieveStatistics(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

  @Override
  public boolean isApplicable(final Update update) {
    return TelegramCommandProcessor.super.isMessageApplicable(update);
  }
}
