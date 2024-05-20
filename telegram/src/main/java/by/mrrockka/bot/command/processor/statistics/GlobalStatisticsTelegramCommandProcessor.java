package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.service.statistics.StatisticsTelegramService;
import org.springframework.stereotype.Component;

@Component("globalStatsTelegramCommandProcessor")
public class GlobalStatisticsTelegramCommandProcessor extends AbstractStatisticsTelegramCommandProcessor {

  public GlobalStatisticsTelegramCommandProcessor(final StatisticsTelegramService statisticsTelegramService) {
    super(statisticsTelegramService);
  }
}

