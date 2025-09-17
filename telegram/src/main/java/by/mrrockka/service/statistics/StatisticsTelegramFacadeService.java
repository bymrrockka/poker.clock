package by.mrrockka.service.statistics;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.parser.StatisticsMessageParser;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class StatisticsTelegramFacadeService {

  private final StatisticsMessageParser statisticsMessageParser;
  private final PersonMentionsValidator personMentionsValidator;
  private final PlayerStatisticsTelegramService playerInGameStatisticsTelegramService;
  private final GlobalPersonStatisticsTelegramService globalPersonStatisticsTelegramService;
  private final GameStatisticsTelegramService gameStatisticsTelegramService;

  public BotApiMethodMessage retrieveStatistics(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageHasNoUserTextMention(messageMetadata);
    final var statistics = statisticsMessageParser.map(messageMetadata);
    return switch (statistics.type()) {
//      case GAME -> gameStatisticsTelegramService.retrieveStatistics(statistics);
//      case PLAYER_IN_GAME -> playerInGameStatisticsTelegramService.retrieveStatistics(statistics);
      case PERSON_GLOBAL -> globalPersonStatisticsTelegramService.retrieveStatistics(statistics);
      default -> throw new RuntimeException("asdasd");
    };
  }

}
