package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.service.statistics.StatisticsTelegramFacadeService;
import org.springframework.stereotype.Component;

@Component("gameStatsTelegramCommandProcessor")
@Deprecated(forRemoval = true)
public class GameStatisticsTelegramCommandProcessor extends AbstractStatisticsTelegramCommandProcessor {

  public GameStatisticsTelegramCommandProcessor(final StatisticsTelegramFacadeService statisticsTelegramService) {
    super(statisticsTelegramService);
  }
}
