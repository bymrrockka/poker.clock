package by.mrrockka.service.statistics;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.mapper.StatisticsMessageMapper;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsTelegramService {

  private final StatisticsMessageMapper statisticsMessageMapper;
  private final PersonMentionsValidator personMentionsValidator;
  private final PlayerInGameStatisticsTelegramService playerInGameStatisticsTelegramService;
  private final GlobalPersonStatisticsTelegramService globalPersonStatisticsTelegramService;
  private final GameStatisticsService gameStatisticsService;

  public BotApiMethodMessage retrieveStatistics(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageHasUserTextMention(messageMetadata);
    final var statistics = statisticsMessageMapper.map(messageMetadata);
    return switch (statistics.type()) {
      case GAME -> gameStatisticsService.retrieveStatistics(statistics);
      case PLAYER_IN_GAME -> playerInGameStatisticsTelegramService.retrieveStatistics(statistics);
      case PERSON_GLOBAL -> globalPersonStatisticsTelegramService.retrieveStatistics(statistics);
    };
  }

}
