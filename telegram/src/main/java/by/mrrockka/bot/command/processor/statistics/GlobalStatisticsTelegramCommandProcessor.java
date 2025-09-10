package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.service.statistics.StatisticsTelegramFacadeService;
import org.springframework.stereotype.Component;

@Component("globalStatsTelegramCommandProcessor")
@Deprecated(forRemoval = true)
public class GlobalStatisticsTelegramCommandProcessor extends AbstractStatisticsTelegramCommandProcessor {

  public GlobalStatisticsTelegramCommandProcessor(final StatisticsTelegramFacadeService statisticsTelegramService) {
    super(statisticsTelegramService);
  }
}

