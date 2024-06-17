package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.service.statistics.StatisticsTelegramFacadeService;
import org.springframework.stereotype.Component;

@Component("myStatsTelegramCommandProcessor")
public class MyStatisticsTelegramCommandProcessor extends AbstractStatisticsTelegramCommandProcessor {

  public MyStatisticsTelegramCommandProcessor(final StatisticsTelegramFacadeService statisticsTelegramService) {
    super(statisticsTelegramService);
  }
}
