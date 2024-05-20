package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.service.statistics.StatisticsTelegramService;
import org.springframework.stereotype.Component;

@Component("gameStatsTelegramCommandProcessor")
public class GameStatisticsTelegramCommandProcessor extends AbstractStatisticsTelegramCommandProcessor {

  public GameStatisticsTelegramCommandProcessor(final StatisticsTelegramService statisticsTelegramService) {
    super(statisticsTelegramService);
  }
}
