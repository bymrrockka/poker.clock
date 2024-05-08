package by.mrrockka.service.statistics;

import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.GlobalPersonStatisticsResponseBuilder;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
class GlobalPersonStatisticsService {

  private final GlobalPersonStatisticsResponseBuilder globalPersonStatisticsResponseBuilder;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    /*todo:
     *  - store calculations in db as it would be a marked that game finished
     *  - write repo to get global person statistics  */

    throw new ProcessingRestrictedException("Route is not implemented");
/*
    return SendMessage.builder()
      .chatId(detailsCommand.metadata().chatId())
      .text(globalPersonDetailsResponseBuilder.response(null))
      .build();*/
  }

}
