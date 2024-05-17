package by.mrrockka.bot.commands;

import by.mrrockka.service.statistics.TelegramStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StatisticsTelegramCommandProcessor implements TelegramCommandProcessor {
  private static final String COMMAND = "^/(game|my)_stats$";
  private final TelegramStatisticsService telegramStatisticsService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramStatisticsService.retrieveStatistics(update);
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
