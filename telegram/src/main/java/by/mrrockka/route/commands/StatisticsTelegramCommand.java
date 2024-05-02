package by.mrrockka.route.commands;

import by.mrrockka.service.statistics.TelegramStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StatisticsTelegramCommand implements TelegramCommand {
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
    return TelegramCommand.super.isMessageApplicable(update);
  }
}
